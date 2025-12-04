import java.security.MessageDigest;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class Addhash{
    private MessageDigest motorHash;
// Método com o propósito de receber um array de bytes onde será percorrido
// e para cada byte dentro do array será convertido para hex
// retornando uma String
    public String retorna_hash(byte[] dados, String algoritmo){
        try{
            motorHash = MessageDigest.getInstance(algoritmo); // Aqui é criado o "motor de hash"
            motorHash.update(dados); // Motor de hash é alimentado com os bytes recebidos
            byte[] hashBytes = motorHash.digest(); // O novo vetor hashBytes contem o hash em forma pura de bytes ,
            StringBuilder textoHash = new StringBuilder(); // Variável para montar o corpo dos bytes convertidos em hash

            int i = 0;
            while(i < hashBytes.length){
                byte tmp = hashBytes[i]; // Váriavel tempóraria para armazenar o byte da posição i
                int valor = tmp & 0xff; // Java o byte vai de -128 a 127 então aqui fazemos com que se torne um inteiro positivo de 0 a 255

                String hex = Integer.toHexString(valor); // O byte da posição i que foi convertido para inteiro aqui é transformado em String
                if(hex.length() == 1){ // Condição necessária para caso para caso tenha apenas 1 caractere 0 é adicionado antes
                    hex = "0" + hex;
                }
                textoHash.append(hex); // Aqui texto recebe i por i o valor do byte em hex
                i++; // Índice recebe incremento
                }
            return textoHash.toString(); // Finalização do metodo que rece uma array de bytes e deseja transcrever em String
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    // salvaHash tem como parametro uma string e tem como objetivo escrever num arquivo .txt
    public boolean salvaHash(String textoHash){
        try (FileWriter escrita = new FileWriter("database.txt", true); // A string é adicionada no final do arquivo sem sobrescrita
        BufferedWriter bufferEscrita = new BufferedWriter(escrita)){ // Melhoria para performance
        bufferEscrita.write(textoHash); // Aqui a hash será escrita em database.txt
        bufferEscrita.newLine(); // Depois de escrita é adicionado uma quebra de linha à hash
        return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
