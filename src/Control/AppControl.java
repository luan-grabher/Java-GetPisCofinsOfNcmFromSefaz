package Control;

import Model.Entity.NcmSefaz;
import Model.Navegacao_Model;
import View.Carregamento;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import main.Arquivo;
import View.View;

public class AppControl {

    private final File arquivo;
    private List<NcmSefaz> ncms = new ArrayList<>();

    public AppControl(File arquivo) {
        this.arquivo = arquivo;
    }

    public void iniciar() {
        Carregamento carregamento = new Carregamento();
        carregamento.setVisible(true);
        
        Carregamento.barra.setMinimum(1);
        Carregamento.barra.setMaximum(3);
        
        Carregamento.barra.setValue(1);
        Carregamento.texto.setText("Montando lista de NCMs...");
        if (montarListaNcms()) {
            Navegacao_Model navegacao = new Navegacao_Model(ncms);
            
            Carregamento.barra.setValue(2);
            Carregamento.texto.setText("Iniciando driver...");
            if(executar(navegacao.iniciar())){
                
                Carregamento.barra.setValue(1);
                Carregamento.texto.setText("Fazendo login...");
                if(executar(navegacao.logarSefaz())){
                    //destroi janela carregamento
                    carregamento.dispose();
                    
                    if(executar(navegacao.pegarValoresNcms())){
                        
                        //CST PIS COFINS
                        if(Arquivo.salvar(arquivo.getParent(), "CST PIS COFINS - " + arquivo.getName(), navegacao.getNcmsAsText(navegacao.getNcmsCST(),"cst"))){
                            View.render("O arquivo de PIS e COFINS foi salvo como 'CST PIS COFINS' na mesma pasta do arquivo original!");
                        }else{
                            View.render("Ocorreu um erro ao salvar o arquivo 'CST PIS COFINS', você está com ele aberto?");
                        }
                        
                        //ICMS SITUAÇÃO TRIBUTARIA
                        if(Arquivo.salvar(arquivo.getParent(), "ICMS Sub Tribut - " + arquivo.getName(), navegacao.getNcmsAsText(navegacao.getNcmsICMS(),"icms"))){
                            View.render("O arquivo de ICMS Subistituição Tributária foi salvo como 'ICMS Sub Tribut' na mesma pasta do arquivo original!");
                        }else{
                            View.render("Ocorreu um erro ao salvar o arquivo 'ICMS Sub Tribut', você está com ele aberto?");
                        }
                        
                        View.render("Programa Terminado!");
                    }
                }
            }
            
            navegacao.finalizar();
        } else {
            View.render("Houve erros ao montar a lista de NCMs.\n"
                    + "Existem valores no arquivo?\n"
                    + "Só existe uma coluna e esta coluna possui os NCMs?", "error");
        }
    }
    
    private boolean executar(String function){
        if(function == ""){
            return true;
        }else{
            View.render(function,"error");
            return false;
        }
    }

    private boolean montarListaNcms() {
        try {
            //Pega texto arquivo e splita
            String textoArquivo = Arquivo.ler(arquivo.getAbsolutePath());
            String[] linhasArquivo = textoArquivo.split("\r\n");

            //percorre linhas para pegar ncms validos
            for (int i = 0; i < linhasArquivo.length; i++) {
                String linha = linhasArquivo[i];
                linha = linha.trim().replaceAll("[^0-9]", "");

                //Se ncm valido
                if (linha.length() == 8) {
                    //adiciona ncm
                    ncms.add(new NcmSefaz(linha));
                }
            }
            
            if(ncms.size() > 0){
                    return true;
            }
        } catch (Exception e) {
        }

        return false;
    }

}
