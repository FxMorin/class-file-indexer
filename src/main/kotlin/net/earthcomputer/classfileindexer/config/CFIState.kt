package net.earthcomputer.classfileindexer.config

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
        name = "net.earthcomputer.classfileindexer.config.CFIState",
        storages = [Storage("class-file-indexer.xml")]
)
class CFIState : PersistentStateComponent<CFIState> {

    @JvmField
    var enabled: Boolean = true
    @JvmField
    var useBlacklist: Boolean = false
    @JvmField
    var useRegex: Boolean = true
    @JvmField
    var paths: MutableList<String> = mutableListOf("^net/minecraft/.*", "^com/mojang/.*")
    @JvmField
    var libraries: MutableList<String> = mutableListOf()

    @Transient
    private var cachedPathRegex: MutableList<Regex> = mutableListOf()
    @Transient
    private var cachedLibraryRegex: MutableList<Regex> = mutableListOf()
    @Transient
    private var needsPathCaching: Boolean = useRegex
    @Transient
    private var needsLibraryCaching: Boolean = useRegex

    override fun getState(): CFIState = this

    override fun loadState(state: CFIState) {
        XmlSerializerUtil.copyBean(state, this)
        if (state.useRegex) { // regex caching
            synchronized(cachedPathRegex) {
                cachePathRegex()
            }
            synchronized(cachedLibraryRegex) {
                cacheLibraryRegex()
            }
        }
    }

    fun canIncludeLibrary(libraryName: String): Boolean {
        if (libraries.isEmpty()) {
            return useBlacklist
        }
        if (useRegex) {
            synchronized(cachedLibraryRegex) {
                if (needsLibraryCaching) { // regex caching
                    cacheLibraryRegex()
                }
                for (regex in cachedLibraryRegex) {
                    if (regex.containsMatchIn(libraryName)) {
                        return !state.useBlacklist
                    }
                }
            }
        } else {
            for (library in libraries) {
                if (libraryName == library) {
                    return !useBlacklist
                }
            }
        }
        return useBlacklist
    }

    fun canIncludeClazz(className: String): Boolean {
        if (paths.isEmpty()) {
            return useBlacklist
        }
        if (useRegex) {
            synchronized(cachedPathRegex) {
                if (needsPathCaching) { // regex caching
                    cachePathRegex()
                }
                for (regex in cachedPathRegex) {
                    if (regex.containsMatchIn(className)) {
                        return !state.useBlacklist
                    }
                }
            }
        } else {
            for (path in paths) {
                if (className == path) {
                    return !useBlacklist
                }
            }
        }
        return useBlacklist
    }

    private fun cacheLibraryRegex() {
        needsLibraryCaching = false
        cachedLibraryRegex = mutableListOf()
        for (path in state.libraries) {
            cachedLibraryRegex.add(Regex(path))
        }
    }

    private fun cachePathRegex() {
        needsPathCaching = false
        cachedPathRegex = mutableListOf()
        for (path in state.paths) {
            cachedPathRegex.add(Regex(path))
        }
    }

    companion object {
        fun getInstance(): CFIState {
            return ApplicationManager.getApplication().getService(CFIState::class.java)
        }
    }
}