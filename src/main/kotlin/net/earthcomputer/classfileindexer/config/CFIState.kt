package net.earthcomputer.classfileindexer.config

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
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

    override fun getState(): CFIState = this

    override fun loadState(state: CFIState) {
        XmlSerializerUtil.copyBean(state, this)
        if (state.useRegex) { // regex caching
            cachedPathRegex = mutableListOf()
            for (path in state.paths) {
                cachedPathRegex.add(Regex(path))
            }
        }
    }

    fun canIncludeClazz(className: String): Boolean {
        if (!state.enabled) {
            return false
        }
        if (state.paths.isEmpty()) {
            return state.useBlacklist
        }
        if (state.useRegex) {
            for (regex in cachedPathRegex) {
                if (regex.containsMatchIn(className)) {
                    return !state.useBlacklist
                }
            }
        } else {
            for (path in state.paths) {
                if (className == path) {
                    return !state.useBlacklist
                }
            }
        }
        return state.useBlacklist
    }

    companion object {
        fun getInstance(): CFIState {
            return ApplicationManager.getApplication().getService(CFIState::class.java)
        }
    }
}