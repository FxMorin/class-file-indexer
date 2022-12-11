package net.earthcomputer.classfileindexer.config

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
        name = "net.earthcomputer.classfileindexer.config.CFIState",
        storages = [Storage("class-file-indexer.xml")]
)
class CFIState : PersistentStateComponent<CFIState.DataState> {

    var dataState: DataState = DataState()
    var cachedPathRegex: MutableList<Regex> = mutableListOf()
    override fun getState(): DataState {
        return dataState
    }

    override fun loadState(state: DataState) {
        XmlSerializerUtil.copyBean(state, dataState)
        if (state.useRegex) { // regex caching
            cachedPathRegex = mutableListOf()
            for (path in if (state.useBlacklist) state.blacklistPaths else state.whitelistPaths) {
                cachedPathRegex.add(Regex(path))
            }
        }
    }

    companion object {
        val instance: CFIState
            get() = ApplicationManager.getApplication().getService(CFIState::class.java)
    }

    inner class DataState {
        var useBlacklist: Boolean = true
        var useRegex: Boolean = true
        var blacklistPaths: List<String> = mutableListOf()
        var whitelistPaths: List<String> = mutableListOf()

        fun canIncludeClazz(className: String): Boolean {
            if (useRegex) {
                for (regex in cachedPathRegex) {
                    if (regex.containsMatchIn(className)) {
                        return !useBlacklist
                    }
                }
                return useBlacklist
            } else {
                for (path in if (useBlacklist) blacklistPaths else whitelistPaths) {
                    if (className == path) {
                        return !useBlacklist
                    }
                }
                return useBlacklist
            }
        }
    }
}