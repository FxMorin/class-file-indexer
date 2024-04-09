package net.earthcomputer.classfileindexer.config

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ex.ApplicationEx
import com.intellij.openapi.options.SearchableConfigurable
import javax.swing.JComponent


class CFISettings : SearchableConfigurable {

    private var panel: CFISettingsPanel? = null

    override fun createComponent(): JComponent {
        return CFISettingsPanel().also { panel = it }.mainPanel
    }

    override fun isModified(): Boolean {
        val panel = this.panel ?: return false
        if (panel.arePathsModified || panel.areLibrariesModified) {
            return true
        }
        val state = CFIState.getInstance()
        return panel.enableClassFileIndexerCheckbox.isSelected != state.enabled
                || panel.useBlacklistCheckbox.isSelected != state.useBlacklist
                || panel.useRegexCheckbox.isSelected != state.useRegex
    }

    override fun reset() {
        val panel = this.panel ?: return
        val state = CFIState.getInstance()
        panel.enableClassFileIndexerCheckbox.isSelected = state.enabled
        panel.useBlacklistCheckbox.isSelected = state.useBlacklist
        panel.useRegexCheckbox.isSelected = state.useRegex

        panel.refreshPathList()
        panel.refreshLibrariesList()
    }

    override fun apply() {
        val panel = this.panel ?: return
        val state = CFIState.getInstance()
        state.enabled = panel.enableClassFileIndexerCheckbox.isSelected
        state.useBlacklist = panel.useBlacklistCheckbox.isSelected
        state.useRegex = panel.useRegexCheckbox.isSelected

        val newList: MutableList<String> = mutableListOf()
        val listModel = panel.pathsList.model
        for (i in 0 until listModel.size) {
            val textField = listModel.getElementAt(i)
            newList.add(textField.text)
        }
        state.paths.clear()
        state.paths.addAll(newList)

        val newLibraryList: MutableList<String> = mutableListOf()
        val listLibraryModel = panel.namesList.model
        for (i in 0 until listLibraryModel.size) {
            val textField = listLibraryModel.getElementAt(i)
            newLibraryList.add(textField.text)
        }
        state.libraries.clear()
        state.libraries.addAll(newLibraryList)

        panel.arePathsModified = false
        panel.areLibrariesModified = false
        // Require restart for changes to take effect  // TODO: Make it not require a restart xD
        (ApplicationManager.getApplication() as ApplicationEx).restart(true)
    }

    override fun disposeUIResources() {
        this.panel = null
    }

    override fun getDisplayName() = "Class File Indexer"
    override fun getId() = "net.earthcomputer.classfileindexer.config.CFISettings"
}