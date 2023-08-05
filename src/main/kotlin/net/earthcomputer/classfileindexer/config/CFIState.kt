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
    var paths: MutableList<String> = mutableListOf("net\\.minecraft\\..*", "com\\.mojang\\..*")

    @Transient
    private var cachedPathRegex: MutableList<Regex> = mutableListOf()
    @Transient
    private var needsCaching: Boolean = useRegex

    override fun getState(): CFIState = this

    override fun loadState(state: CFIState) {
        XmlSerializerUtil.copyBean(state, this)
        if (state.useRegex) { // regex caching
            cacheRegex()
        }
    }

    fun canIncludeClazz(className: String): Boolean {
        if (paths.isEmpty()) {
            return useBlacklist
        }
        if (useRegex) {
            if (needsCaching) { // regex caching
                cacheRegex()
            }
            for (regex in cachedPathRegex) {
                if (regex.containsMatchIn(className)) {
                    return !state.useBlacklist
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

    private fun cacheRegex() {
        needsCaching = false
        cachedPathRegex = mutableListOf()
        for (path in state.paths) {
            println(path)
            cachedPathRegex.add(Regex(path))
        }
    }

    companion object {
        fun getInstance(): CFIState {
            return ApplicationManager.getApplication().getService(CFIState::class.java)
        }
    }
}