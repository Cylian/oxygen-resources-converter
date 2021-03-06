package com.oxygenxml.resources.batch.converter.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oxygenxml.resources.batch.converter.ConverterTypes;
import com.oxygenxml.resources.batch.converter.persister.OptionTags;
import com.oxygenxml.resources.batch.converter.translator.Tags;

/**
 * Provider for the additional options presented in the conversion dialog.
 *  
 * @author cosmin_duna
 */
public class ConverterAdditionalOptionsProvider {
  
  /**
   * Map between options and translation tags.
   */
  private static Map<String, String> addionalOptionToTranslationTag = new HashMap<String, String>();
  static {
    addionalOptionToTranslationTag.put(OptionTags.CREATE_DITA_MAP_FROM_WORD, Tags.CREATE_DITA_MAP_OPTION);
  }
  
  /**
   * Private constructor.
   */
  private ConverterAdditionalOptionsProvider() {
    // Avoid instantiation.
  }
  
  /**
   * The the imposed options according to given conversion type.
   * @param convertionType The conversion type.
   * 
   * @return The additional imposed options.
   */
  public static final List<String> getImposedAdditionalOptions(String convertionType){
    List<String> options= new ArrayList<String>();
    if(ConverterTypes.WORD_TO_DITA.equals(convertionType)) {
      options.add(OptionTags.CREATE_DITA_MAP_FROM_WORD);
    }
    return options;
  }
  
  /**
   * Get the translation tag associated with the given additional option.
   * 
   * @param additionalOption The additional option.
   * 
   * @return The translation tag associated with the option.
   */
  public static final String getTranslationTagFor(String additionalOption) {
    return addionalOptionToTranslationTag.get(additionalOption);
  }
}
