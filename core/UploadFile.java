import java.nio.file.Files;
import java.nio.file.Paths;

public boolean uploadFile(String caminhoArquivo, String algoritmo){
    try{
        byte[] dados = Files.readAllBytes(Paths.get(caminhoArquivo));
        String hashGerada = retorna_hash(dados, algoritmo);
        if(hashGerada == null){
            return false;
        }

        boolean salvou = salvaHash(hashGerada);
        return salvou;
    }
    catch(Exception e){
        e.printStackTrace();
        return false;
    }
}
