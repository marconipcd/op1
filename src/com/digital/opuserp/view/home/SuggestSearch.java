package com.digital.opuserp.view.home;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.digital.opuserp.dao.ClienteDAO;
import com.digital.opuserp.domain.Cliente;
//import com.librishuffle.SuggestBox;
//import com.librishuffle.shared.SuggestionDto;

//public class SuggestSearch extends SuggestBox {
//
//	@Override
//	public void selectionChanged(int itemId) {
//		
//		
//	}
//	
//	 private final Set<SuggestionDto> SUGGESTIONS = 	new HashSet(Arrays.asList(new String[] { }));
//
//	@Override
//	protected Set<SuggestionDto> getSuggestions(String query) {
//		final String queryLower = query.toLowerCase();
//
//		List<Cliente> clientes = ClienteDAO.search(queryLower);
//		SUGGESTIONS.clear();
//
//		int i= 0;
//		for (Cliente cliente : clientes) {			
//			SUGGESTIONS.add(new SuggestionDto(i, cliente.getNome_razao()+" "+cliente.getTelefone1()));
//		}
//		
//		return SUGGESTIONS;
//	}
//
//}
