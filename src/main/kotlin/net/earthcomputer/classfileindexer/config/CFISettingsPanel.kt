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
    lateinit var buttonList: JList<JButton>

    init {
        pluginSettingsPanel.border = IdeBorderFactory.createTitledBorder("Plugin Settings")
        addPathPanel.border = IdeBorderFactory.createTitledBorder("Add Path")
        pathsScrollPane.border = IdeBorderFactory.createTitledBorder("Paths")
        val gridBagLayout = GridBagLayout()
        val pathConstraints = GridBagConstraints()
        pathConstraints.weightx = 0.7
        pathConstraints.fill = GridBagConstraints.HORIZONTAL
        pathConstraints.anchor = GridBagConstraints.NORTH
        pathConstraints.weighty = 1.0
        val buttonConstraints = GridBagConstraints()
        buttonConstraints.weighty = 1.0
        buttonConstraints.anchor = GridBagConstraints.NORTH
        buttonConstraints.fill = GridBagConstraints.NONE
        gridBagLayout.setConstraints(pathsList, pathConstraints)
        gridBagLayout.setConstraints(buttonList, buttonConstraints)
        pathsPanel.layout = gridBagLayout

        pathsList.cellRenderer = TextFieldListRenderer()
        buttonList.cellRenderer = ButtonListRenderer()

        buttonList.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(event: MouseEvent) {
                val index: Int = buttonList.locationToIndex(event.getPoint())
                CFIState.getInstance().paths.removeAt(index)
                arePathsModified = true
                refreshList()
            }
        })

        addPathButton.addActionListener {
            CFIState.getInstance().paths.add(pathTextField.text)
            arePathsModified = true
            pathTextField.text = ""
            refreshList()
        }

        refreshList()
    }

    fun refreshList() {
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
        buttonList.model = buttonModel

        val totalCount = CFIState.getInstance().paths.count()
        pathsList.visibleRowCount = totalCount
        buttonList.visibleRowCount = totalCount

        addPathPanel.repaint()
        pathsPanel.revalidate()
        pathsPanel.repaint()
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
