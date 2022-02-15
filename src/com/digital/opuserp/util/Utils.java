package com.digital.opuserp.util;

public class Utils {

	public static String preencheCom(String linha_a_preencher, String letra, int tamanho, int direcao){

        //Checa se Linha a preencher é nula ou branco

        if (linha_a_preencher == null || linha_a_preencher.trim() == "" ) {linha_a_preencher = "";}

       

        //Enquanto Linha a preencher possuir 2 espaços em branco seguidos, substitui por 1 espaço apenas

        while (linha_a_preencher.contains(" ")) {linha_a_preencher = linha_a_preencher.replaceAll(" "," ").trim();}

       

        //Retira caracteres estranhos

        linha_a_preencher = linha_a_preencher.replaceAll("[./-]","");

        StringBuffer sb = new StringBuffer(linha_a_preencher);

        if (direcao==1){ //a Esquerda

            for (int i=sb.length() ; i<tamanho ; i++){

                sb.insert(0,letra);

            }

        } else if (direcao==2) {//a Direita

            for (int i=sb.length() ; i<tamanho ; i++){

                sb.append(letra);

            }

        }

        return sb.toString();

    }




}
