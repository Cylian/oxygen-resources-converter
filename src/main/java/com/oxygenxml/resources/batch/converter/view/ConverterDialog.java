package com.oxygenxml.resources.batch.converter.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.oxygenxml.resources.batch.converter.BatchConverterInteractor;
import com.oxygenxml.resources.batch.converter.persister.ContentPersister;
import com.oxygenxml.resources.batch.converter.persister.ContentPersisterImpl;
import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;
import com.oxygenxml.resources.batch.converter.worker.ConverterWorker;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.ui.OKCancelDialog;

/**
 * Converter dialog.
 * @author Cosmin Duna
 *
 */
public class ConverterDialog extends OKCancelDialog implements BatchConverterInteractor{

	/**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The input panel.
	 */
	private InputPanel inputPanel;
	
	/**
	 * The output panel.
	 */
	private	OutputPanel outputPanel;

	/**
	 * Converter worker.
	 */
	private transient ConverterWorker converterWorker;
	
	/**
	 * CheckBox for select to open converted files after conversion.
	 */
	private JCheckBox openFilesCBox;
	
	/**
	 * Translator.
	 */
	private transient Translator translator;

	/**
	 * The type of converter.
	 */
	private String converterType;
	
	/**
	 * Used for persistence.
	 */
	private transient ContentPersister contentPersister;

	/**
	 * Link to GitHub repository description.
	 */
	private static final String LINK_TO_GIT_HUB = "https://github.com/oxygenxml/oxygen-resources-convertor";
	
	/**
	 * The additional 
	 */
	private Map<String, JCheckBox> additionalOptions = new HashMap<String, JCheckBox>();
	
	/**
	 * Constructor.
	 * @param converterType The type of converter.
	 * @param toConvertFiles List with files to convert.
	 * @param parentFrame The parent frame.
	 * @param translator Translator.
	 */
	public ConverterDialog(String converterType, List<File> toConvertFiles, JFrame parentFrame, Translator translator) {
		super(parentFrame, "" , true);
		this.converterType = converterType;
		this.translator = translator;
		contentPersister = new ContentPersisterImpl();
		
		inputPanel = new InputPanel(converterType, translator, this);
		outputPanel = new OutputPanel(translator);
		openFilesCBox = new JCheckBox(translator.getTranslation(Tags.OPEN_FILE_CHECK_BOX , ""));
		
		initGUI();
		
		//  Load saved state of the dialog
		contentPersister.loadState(this);
		getOkButton().setEnabled(!toConvertFiles.isEmpty());
		
		setTitle(translator.getTranslation(Tags.MENU_ITEM_TEXT, converterType));
		setOkButtonText(translator.getTranslation(Tags.CONVERT_BUTTON, ""));
		setResizable(true);
		pack();
		setMinimumSize(new Dimension(getSize().width , getSize().height));
		setLocationRelativeTo(parentFrame);
		
		if (!toConvertFiles.isEmpty()){
			// Set the input files and the output folder
			inputPanel.addFilesInTable(toConvertFiles);
			setOutputFolder(toConvertFiles.get(toConvertFiles.size()-1).getParent() + File.separator + "output");
		}
		
		setVisible(true);
	}

	/**
	 * Initialize the GUI.
	 * 
	 */
	private void initGUI(){
		
		JPanel convertorPanel = new JPanel( new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		//-----Add the input panel
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 7, 0);
		convertorPanel.add(inputPanel, gbc);
	
		//-----Add the output panel
		gbc.gridy++;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		convertorPanel.add(outputPanel, gbc);
		
		//----Add the checkBox for select to open converted files after conversion 
		gbc.gridy++;
		convertorPanel.add(openFilesCBox, gbc);
		
		List<String> imposedAdditionalOptions = ConverterAdditionalOptionsProvider.getImposedAdditionalOptions(converterType);
		for (String imposedOption : imposedAdditionalOptions) {
		  JCheckBox optionCombo = new JCheckBox(translator.getTranslation(
		      ConverterAdditionalOptionsProvider.getTranslationTagFor(imposedOption), ""));
		  additionalOptions.put(imposedOption, optionCombo);
		  gbc.gridy++;
	    convertorPanel.add(optionCombo, gbc);
    }
		
		this.add(convertorPanel);
	}
	
	/**
	 * Convert pressed.
	 */
	@Override
	protected void doOK() {

		if (outputPanel.getOutputPath().isEmpty()) {
			//output panel is empty.
			//show a warning message.
			PluginWorkspaceProvider.getPluginWorkspace().showWarningMessage(translator.getTranslation(Tags.EMPTY_OUTPUT_MESSAGE,""));
		} else {

			//create a progress dialog
			final ProgressDialog progressDialog = new ProgressDialog((JFrame)super.getParent(), translator, converterType);

			//create a converter worker.
			converterWorker = new ConverterWorker(converterType, this, progressDialog);

			//add a action listener on cancel button for progress dialog
			progressDialog.addCancelActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					converterWorker.cancel(true);
					progressDialog.dispose();
				}
			});

			//start the worker.
			converterWorker.execute();

			// Save the state of dialog.
			contentPersister.saveState(this);
			
			super.doOK();
		}
	}

	@Override
	public List<File> getInputFiles() {
		return inputPanel.getFilesFromTable();
	}


	@Override
	public File getOutputFolder() {
		return new File(outputPanel.getOutputPath());
	}


	@Override
	public void setOutputFolder(String text) {
		outputPanel.setOutputPath(text);
	}


	@Override
	public void setEnableConvert(boolean state) {
		getOkButton().setEnabled(state);
	}

	@Override
	public String getHelpPageID() {
		return LINK_TO_GIT_HUB;
	}

	@Override
	public boolean mustOpenConvertedFiles() {
		return openFilesCBox.isSelected();
	}
	
  @Override
  public void setOpenConvertedFiles(boolean state) {
    openFilesCBox.setSelected(state);
  }

	@Override
	public Boolean getAdditionalOptionValue(String additionalOptionId) {
	  Boolean toRet = null;
	  JCheckBox optionCombo = additionalOptions.get(additionalOptionId);
	  if (optionCombo != null) {
	    toRet = optionCombo.isSelected();
	  }
	  return toRet;
	}
	
	@Override
	public Set<String> getAdditionalOptions() {
	  return additionalOptions.keySet();
	}
	
	@Override
	public void setAdditionalOptionValue(String additionalOptionId, boolean state) {
	  JCheckBox optionCombo = additionalOptions.get(additionalOptionId);
    if (optionCombo != null) {
     optionCombo.setSelected(state);
    }
	}
	

}
