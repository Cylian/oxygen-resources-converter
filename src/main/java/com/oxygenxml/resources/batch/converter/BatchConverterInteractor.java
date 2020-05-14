package com.oxygenxml.resources.batch.converter;

import java.util.Set;

/**
 * Interactor with batch converter.
 * @author Cosmin Duna
 *
 */
public interface BatchConverterInteractor extends UserInputsProvider{

	/**
	 * Set the output folder path.
	 * @param text The path of output folder.
	 */
	public void setOutputFolder(String text);
	
	/**
   * Set the selected value for the given additional option.
   * 
   * @param additionalOptionId The id of the additional option.
   * @param state <code>true</code> to set selected.
   */
  public void setAdditionalOptionValue(String additionalOptionId, boolean state);
	
  /**
   * Get all additional options used in this conversion.
   * 
   * @return All additional options
   */
	public Set<String> getAdditionalOptions();
	
	/**
	 * Set if the converted file must be opened.
	 * @param <code>true</code> if converted files must be opened, <code>false</code>otherwise.
	 */
	public void setOpenConvertedFiles(boolean state);
	
	/**
	 * Set enable/ disable the convert button.
	 * @param state <code>true</code> to set enable, <code>false</code> to set disable.
	 */
	public void setEnableConvert(boolean state);
}
