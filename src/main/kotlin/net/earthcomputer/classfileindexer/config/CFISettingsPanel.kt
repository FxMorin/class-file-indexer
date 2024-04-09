package net.earthcomputer.classfileindexer.config

import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.JBCheckBox
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*


class CFISettingsPanel {

    var arePathsModified: Boolean = false
    var areLibrariesModified: Boolean = false

    lateinit var mainPanel: JPanel
    lateinit var pluginSettingsPanel: JPanel
    lateinit var enableClassFileIndexerCheckbox: JBCheckBox
    lateinit var useBlacklistCheckbox: JBCheckBox
    lateinit var useRegexCheckbox: JBCheckBox

    lateinit var addPathPanel: JPanel
    lateinit var pathTextField: JTextField
    lateinit var addPathButton: JButton
    lateinit var pathsScrollPane: JScrollPane
    lateinit var pathsPanel: JPanel
    lateinit var pathsList: JList<JTextField>
    lateinit var pathButtonList: JList<JButton>

    lateinit var addLibraryNamePanel: JPanel
    lateinit var nameTextField: JTextField
    lateinit var addNameButton: JButton
    lateinit var namesScrollPane: JScrollPane
    lateinit var namesPanel: JPanel
    lateinit var namesList: JList<JTextField>
    lateinit var nameButtonList: JList<JButton>

    init {
        pluginSettingsPanel.border = IdeBorderFactory.createTitledBorder("Plugin Settings")
        addPathPanel.border = IdeBorderFactory.createTitledBorder("Add Path")
        pathsScrollPane.border = IdeBorderFactory.createTitledBorder("Paths")
        addLibraryNamePanel.border = IdeBorderFactory.createTitledBorder("Add Library Names")
        namesScrollPane.border = IdeBorderFactory.createTitledBorder("Library Names")



        val gridBagLayout = GridBagLayout()
        val gridBagLayout2 = GridBagLayout()
        gridBagLayout.setConstraints(pathsList, createListItemConstraints())
        gridBagLayout.setConstraints(pathButtonList, createButtonConstraints())
        pathsPanel.layout = gridBagLayout
        gridBagLayout2.setConstraints(namesList, createListItemConstraints())
        gridBagLayout2.setConstraints(nameButtonList, createButtonConstraints())
        namesPanel.layout = gridBagLayout2

        pathsList.cellRenderer = TextFieldListRenderer()
        pathButtonList.cellRenderer = ButtonListRenderer()

        namesList.cellRenderer = TextFieldListRenderer()
        nameButtonList.cellRenderer = ButtonListRenderer()

        pathButtonList.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(event: MouseEvent) {
                val index: Int = pathButtonList.locationToIndex(event.getPoint())
                CFIState.getInstance().paths.removeAt(index)
                arePathsModified = true
                refreshPathList()
            }
        })

        nameButtonList.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(event: MouseEvent) {
                val index: Int = nameButtonList.locationToIndex(event.getPoint())
                CFIState.getInstance().libraries.removeAt(index)
                areLibrariesModified = true
                refreshLibrariesList()
            }
        })

        addPathButton.addActionListener {
            CFIState.getInstance().paths.add(pathTextField.text)
            arePathsModified = true
            pathTextField.text = ""
            refreshPathList()
        }

        addNameButton.addActionListener {
            CFIState.getInstance().libraries.add(nameTextField.text)
            areLibrariesModified = true
            nameTextField.text = ""
            refreshLibrariesList()
        }

        refreshPathList()
        refreshLibrariesList()
    }

    fun refreshPathList() {
        val pathModel: DefaultListModel<JTextField> = DefaultListModel<JTextField>()
        val buttonModel: DefaultListModel<JButton> = DefaultListModel<JButton>()

        for (path in CFIState.getInstance().paths) {
            val textField = JTextField(path, 50)
            textField.isEditable = false
            textField.isFocusable = false
            textField.horizontalAlignment = JTextField.LEFT
            pathModel.addElement(textField)
            buttonModel.addElement(JButton("Remove"))
        }

        pathsList.model = pathModel
        pathButtonList.model = buttonModel

        val totalCount = CFIState.getInstance().paths.count()
        pathsList.visibleRowCount = totalCount
        pathButtonList.visibleRowCount = totalCount

        pathsPanel.revalidate()
        pathsScrollPane.revalidate()

        pathsPanel.repaint()
        pathsScrollPane.repaint()
        addPathPanel.repaint()
    }

    fun refreshLibrariesList() {
        val nameModel: DefaultListModel<JTextField> = DefaultListModel<JTextField>()
        val buttonModel: DefaultListModel<JButton> = DefaultListModel<JButton>()

        for (library in CFIState.getInstance().libraries) {
            val textField = JTextField(library, 50)
            textField.isEditable = false
            textField.isFocusable = false
            textField.horizontalAlignment = JTextField.LEFT
            nameModel.addElement(textField)
            buttonModel.addElement(JButton("Remove"))
        }

        namesList.model = nameModel
        nameButtonList.model = buttonModel

        val totalCount = CFIState.getInstance().libraries.count()
        namesList.visibleRowCount = totalCount
        nameButtonList.visibleRowCount = totalCount

        namesPanel.revalidate()

        namesScrollPane.setViewportView(namesPanel)

        namesScrollPane.revalidate()

        namesPanel.repaint()
        namesScrollPane.repaint()
        addLibraryNamePanel.repaint()
    }

    private fun createListItemConstraints() : GridBagConstraints {
        val constraints = GridBagConstraints()
        constraints.weightx = 0.7
        constraints.fill = GridBagConstraints.BOTH
        constraints.anchor = GridBagConstraints.NORTH
        constraints.weighty = 1.0
        return constraints
    }

    private fun createButtonConstraints() : GridBagConstraints {
        val constraints = GridBagConstraints()
        constraints.weighty = 1.0
        constraints.anchor = GridBagConstraints.NORTH
        constraints.fill = GridBagConstraints.NONE
        return constraints
    }

    internal class TextFieldListRenderer : JTextField(), ListCellRenderer<JTextField> {
        override fun getListCellRendererComponent(
            comp: JList<out JTextField>, value: JTextField, index: Int,
            isSelected: Boolean, hasFocus: Boolean
        ): Component {
            setEnabled(comp.isEnabled)
            setFont(comp.font)
            text = value.text
            if (isSelected) {
                setBackground(comp.selectionBackground)
                setForeground(comp.selectionForeground)
            } else {
                setBackground(comp.getBackground())
                setForeground(comp.getForeground())
            }
            return this
        }
    }

    internal class ButtonListRenderer : JButton(), ListCellRenderer<JButton> {
        override fun getListCellRendererComponent(
            comp: JList<out JButton>, value: JButton, index: Int,
            isSelected: Boolean, hasFocus: Boolean
        ): Component {
            setEnabled(comp.isEnabled)
            setFont(comp.font)
            setText(value.text)
            if (isSelected) {
                setBackground(comp.selectionBackground)
                setForeground(comp.selectionForeground)
            } else {
                setBackground(comp.getBackground())
                setForeground(comp.getForeground())
            }
            return this
        }
    }
}
