package com.digital.opuserp.converter;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

public class StringToInteger implements Converter<String, Integer>{

	@Override
	public Integer convertToModel(String value,
			Class<? extends Integer> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if(value == null)
			return null;
					
			return Integer.parseInt(value); 
	}

	@Override
	public String convertToPresentation(Integer value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if(value == null)
			return null;
						
			return value.toString(); 
	}

	@Override
	public Class<Integer> getModelType() {
		// TODO Auto-generated method stub
		return Integer.class;
	}

	@Override
	public Class<String> getPresentationType() {
		// TODO Auto-generated method stub
		return String.class;
	}

	
}
