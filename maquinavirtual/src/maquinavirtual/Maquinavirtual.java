/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maquinavirtual;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * @author lucas correa
 */
public class Maquinavirtual {

    /**
     * @param args the command line arguments
     */
    
    /********************************************************************************

Máquina virtual ILT

	Este codigo implementa uma máquina virtual (interpretador) que a partir de um arquivo
        txt, vai ler e manipular os dados da memoria e ler de outro arquivo as instruções
        a serem realizadas, decodificando e executando para salvar as informações no
        arquivo txt de dados.

***********************************************************************************

Detalhes do set de instrução

	Tamanho das instruções: 8 bits
	
	Código das intruções:
	
		ADD: 	00
		SUB: 	01
		LOAD: 	10
		STORE:	11

	Instruções Tipo 1: 
	
		- Utilizado para operaçções aritméticas (soma e subtração)
	     
             MSB                                      LSB
		   
		(Tipo instr.) (End. Reg 1) (End. Reg 2) (End Reg Dest.)
          
                   2 bits        2 bits        2 bits       2 bits
           
		   
         - Exemplo: 00011011 >>> |00|01|10|11
         
         	 	Realiza a soma (00 >> tipo da instrução) do registro 0 (01 
 	 	 	 >> end. Reg 1) com o registro 1 (10 >> end. Reg 2) e salva o resultado
 	 	 	 em registro 2 (11 >> end. Reg Dest.)
 	 	 	 
 	 	 	 
    Instruções Tipo 2:
    
     	 - Uitlizado para operações de LOAD e STORE
     	 
     	       MSB                        LSB
     	 
     	 (Tipo instr.) (End Reg) (End Memória de dados)

            2 bits       2 bits        4 bits
		    
   	   - Exemplo: 10010100 >>> |10|01|0100
         
         	 	Realiza o LOAD (10 >> tipo da instrução) do endereço de 
			memória 4 (0100 >> end. Memória) para o registro 0 
			(01 >> end. Reg 1)
    Os dados da memória serão salvos em um arquivo txt
    Existem 4 registadores: R0, R1, R2, R3
********************************************************************************/
    
    public static int memoria[] = new int[16];
    public static int registrador[] = new int[4];
    
    public static void main(String[] args) {
        
        int instr_type=0;
        int data_loc=0; //pega o valor do primeiro registrador
        int data_loc2=0; //pega o valor do segundo registrador caso seja SUB ou ADD
        int data_loc3=0; //pega o valor do terceiro registrador caso seja SUB ou ADD
        int data=0; //valor que sera salvo o primeiro dado do registrador
        int data2=0; //valor que sera salvo o segundo dado do registrador
        int flag=0; //comparador para saber se chamou a função find_data mais de uma vez
 
        for(int i=0;i<4;i++){
            
            registrador[i] = 0;
        }
        Scanner ler1 = new Scanner(System.in);
        System.out.printf("Informe o nome de arquivo de dados:\n"); //nome do arquivo tem que conter o endereço que ele está no computador
        String nome1 = ler1.nextLine();
        try {
            FileReader arq1 = new FileReader(nome1);
            BufferedReader lerArq1 = new BufferedReader(arq1);
            String linha1 = lerArq1.readLine();
            try {
                
                for(int i=0;i<16;i++){

                memoria[i] = Integer.parseInt(linha1); 
                linha1 = lerArq1.readLine();
            }
             } catch (NumberFormatException e) {
               System.out.println("Numero com formato errado!");
             }
            
            Scanner lerA = new Scanner(System.in);
            System.out.printf("Informe o nome de arquivo de instrucao:\n");
            String nomeA = lerA.nextLine();
            
            /*Scanner ler = new Scanner(System.in);
            System.out.printf("Informe o nome de arquivo de cache:\n");
            String nome = ler.nextLine(); */

            String nome = new String();
            nome = ("cache.txt");
            
            FileReader arq2 = new FileReader(nomeA);
            BufferedReader lerArq2 = new BufferedReader(arq2);
            String linha2 = lerArq2.readLine();
            
            FileWriter fileW = new FileWriter (nome);//arquivo para escrita
            BufferedWriter buffW = new BufferedWriter (fileW);
            try {
                
                while (linha2 != null) {
                     
                buffW.write(linha2);
                buffW.newLine();
                linha2 = lerArq2.readLine();
             }
                
            }
              catch (NumberFormatException e) {
               System.out.println("Erro na cache!");
             }
            lerArq2.close();
            buffW.close ();

            try {
                FileReader arq = new FileReader(nome);
                BufferedReader lerArq = new BufferedReader(arq);
                String linha = lerArq.readLine();

             while (linha != null) {
                flag = 0;
                System.out.printf("\nLinha de instrução: " + linha + "\n");
                String cod = linha.substring(0, 2);
                instr_type = get_instr_type(cod);
                data_loc = find_data(linha, instr_type, flag);
                flag++;
                data_loc2 = find_data(linha, instr_type, flag);
                flag++;
                if(instr_type == 1 || instr_type == 2){

                    data_loc3 = find_data(linha, instr_type, flag);
                }
                if(data_loc >= 0 && data_loc2 >= 0 && instr_type == 1 || instr_type == 2){

                    data = registrador[data_loc];
                    data2 = registrador[data_loc2];
                }
                if(data_loc >= 0 && data_loc2 >=0 && instr_type == 3){

                    data = memoria[data_loc2];

                }

                if(data_loc >= 0 && data_loc >= 0 && instr_type == 4){

                    data = registrador[data_loc];

                }

                execute(instr_type, data, data2, data_loc, data_loc2, data_loc3);

                linha = lerArq.readLine(); // lê da segunda até a última linha

             }
             System.out.println("Valor final da memória:\n");
             for(int i=0;i<16;i++){

                 System.out.println("Pos: "+i+" Valor: "+memoria[i]);
            }
             arq.close();
            } catch (IOException e) {
                System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
            }
            try{
            BufferedWriter bf = new BufferedWriter(new FileWriter (nome1));
            for(int i = 0; i < 16; i++){
                bf.write(Integer.toString(memoria[i]));
                if(i < 15) bf.newLine();
            }
            bf.close(); 
          
            }catch (IOException e){
               System.err.printf("Erro no salvamento do arquivo: %s.\n", e.getMessage());
            }
            arq1.close();
        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
        }
    }
    
    public static int get_instr_type(String codigo1){
        
        if(codigo1.equals("00")){
            
            System.out.println("Tipo de Instrução: ADD");
            return 1; //1 = função ADD
        }
        if(codigo1.equals("01")){
            
            System.out.println("Tipo de Instrução: SUB");
            return 2; //2 = função SUB
        }
        if(codigo1.equals("10")){
            
            System.out.println("Tipo de Instrução: LOAD");
            return 3; //3 = função LW
        }
        if(codigo1.equals("11")){
            
            System.out.println("Tipo de Instrução: STORE");
            return 4; //4 = função SW
        }
        
        return 0;
    }
    public static void execute(int type, int data, int data2,int data_loc, int data_loc2, int data_loc3){
        
        if(type == 1){ //função ADD
            
            data  = data + data2;
            registrador[data_loc3] = data;
            System.out.println("\nRegistrador> pos: "+ data_loc3 + " Valor: "+ registrador[data_loc3]);
        }
        if(type == 2){ //função SUB
            
            data = data - data2;
            registrador[data_loc3] = data;
            System.out.println("\nRegistrador> pos: "+ data_loc3 + " Valor: "+ registrador[data_loc3]);
        }
        if(type == 3){ //função LW
            
            registrador[data_loc] = data;
            System.out.println("\nRegistrador> pos: "+ data_loc + " Valor: "+ registrador[data_loc]);
        }
        if(type == 4){ //função SW            
            
            memoria[data_loc2] = data;
            System.out.println("\nMemoria> pos: "+ data_loc2 + " Valor: "+ memoria[data_loc2]);
        }
    }
    public static int find_data(String instr, int type, int flag){
        
        if(type == 1){ //função ADD
           
           if(flag == 0) instr = instr.substring(2, 4);
           else if(flag == 1) instr = instr.substring(4, 6);
           else instr = instr.substring(6, 8);
           
           if(instr.equals("00")){
               
               return 0;
           }
           if(instr.equals("01")){
               
               return 1;
           }
           if(instr.equals("10")){
               
               return 2;
           }
           if(instr.equals("11")){
               
               return 3;
           }
            
        }
        if(type == 2){ //função SUB
            
            if(flag == 0) instr = instr.substring(2, 4);
           else if(flag == 1) instr = instr.substring(4, 6);
           else instr = instr.substring(6, 8);
           if(instr.equals("00")){
               
               return 0;
           }
           if(instr.equals("01")){
               
               return 1;
           }
           if(instr.equals("10")){
               
               return 2;
           }
           if(instr.equals("11")){
               
               return 3;
           }
        }
        if(type == 3){ //função LW
           
           if(flag == 0){
               
               instr = instr.substring(2, 4);
              
               if(instr.equals("00")){

                   return 0;
               }
               if(instr.equals("01")){

                   return 1;
               }
               if(instr.equals("10")){

                   return 2;
               }
               if(instr.equals("11")){

                   return 3;
               }
               
           }else {
               
               instr = instr.substring(4, 8);
               
               if(instr.equals("0000")){
               
                    return 0;
                }
                if(instr.equals("0001")){

                    return 1;
                }
                if(instr.equals("0010")){

                    return 2;
                }
                if(instr.equals("0011")){

                    return 3;
                }
                if(instr.equals("0100")){

                    return 4;
                }
                if(instr.equals("0101")){

                    return 5;
                }
                if(instr.equals("0110")){

                    return 6;
                }
                if(instr.equals("0111")){

                    return 7;
                }
                if(instr.equals("1000")){

                    return 8;
                }
                if(instr.equals("1001")){

                    return 9;
                }
                if(instr.equals("1010")){

                    return 10;
                }
                if(instr.equals("1011")){

                    return 11;
                }
                if(instr.equals("1100")){

                    return 12;
                }
                if(instr.equals("1101")){

                    return 13;
                }
                if(instr.equals("1110")){

                    return 14;
                }
                if(instr.equals("1111")){

                    return 15;
                }
           }
        }
        if(type == 4){ //função SW
            
           if(flag == 0){
               
               instr = instr.substring(2, 4);
              
               if(instr.equals("00")){

                   return 0;
               }
               if(instr.equals("01")){

                   return 1;
               }
               if(instr.equals("10")){

                   return 2;
               }
               if(instr.equals("11")){

                   return 3;
               }
               
           }else {
               
               instr = instr.substring(4, 8);
               
               if(instr.equals("0000")){
               
                    return 0;
                }
                if(instr.equals("0001")){

                    return 1;
                }
                if(instr.equals("0010")){

                    return 2;
                }
                if(instr.equals("0011")){

                    return 3;
                }
                if(instr.equals("0100")){

                    return 4;
                }
                if(instr.equals("0101")){

                    return 5;
                }
                if(instr.equals("0110")){

                    return 6;
                }
                if(instr.equals("0111")){

                    return 7;
                }
                if(instr.equals("1000")){

                    return 8;
                }
                if(instr.equals("1001")){

                    return 9;
                }
                if(instr.equals("1010")){

                    return 10;
                }
                if(instr.equals("1011")){

                    return 11;
                }
                if(instr.equals("1100")){

                    return 12;
                }
                if(instr.equals("1101")){

                    return 13;
                }
                if(instr.equals("1110")){

                    return 14;
                }
                if(instr.equals("1111")){

                    return 15;
                }
           }
           }
        return 0;
    }
}