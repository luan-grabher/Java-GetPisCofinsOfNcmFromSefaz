package main;

import Control.AppControl;
import View.View;
import java.io.File;

public class BuscarPISCOFINSSEFAZ {
    
    public static void main(String[] args) {
        try{
            //Inicia Aplicação

            //Solicita Arquivo CSV
            View.render("Por favor, selecione o arquivo csv com todos NCMs.\nOBS: Só deve existir uma coluna.");
            File arquivo = Selector.Arquivo.selecionar("C:/Users/", "Separado Por Virgulas (CSV)", "csv");
            
            if(Selector.Arquivo.verifica(arquivo.getAbsolutePath(), "csv")){
                AppControl app = new AppControl(arquivo);
                app.iniciar();
                //termina aplicação
            }
        }catch(Exception e){
            View.render("Ocorreu um erro desconhecido no programa! Erro: "  + e, "error");
        }
        
        System.exit(0);
    }
    
}
