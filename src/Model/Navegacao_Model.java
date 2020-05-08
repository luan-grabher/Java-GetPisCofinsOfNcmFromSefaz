package Model;

import Model.Entity.NcmSefaz;
import View.Carregamento;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chromeclass.SeleniumChrome;
import org.openqa.selenium.wait.wait;

public class Navegacao_Model {

    private List<NcmSefaz> ncms;
    private List<NcmSefaz> ncmsCST = new ArrayList<>();
    private List<NcmSefaz> ncmsICMS = new ArrayList<>();

    private WebElement e;
    private List<WebElement> es;
    private SeleniumChrome chrome = null;

    public Navegacao_Model(List<NcmSefaz> ncms) {
        this.ncms = ncms;
    }

    public String iniciar() {
        //Busca driver
        //File arquivoDriver = new File("G:\\Informatica\\Programas\\APLICATIVOS MORESCO\\Selenium WebDriver\\chromedriver.exe");

        //if (arquivoDriver.exists()) {
            chrome = new SeleniumChrome();
            if (chrome.abrirChrome("http://www.econeteditora.com.br/user/login.asp?Pag=/")) {
                //Esconde navegador
                chrome.getChrome().manage().window().setPosition(new Point(-10000, 0));
                return "";
            } else {
                return "Erro ao abrir o navegador!";
            }
        //} else {
            //return "ChromeDriver não encontrado em:\n" + arquivoDriver.getAbsolutePath();
        //}
    }

    public void finalizar() {
        try {
            chrome.fecharChrome();
        } catch (Exception e) {
        }
    }

    public String logarSefaz() {
        String user = "";
        String senha = "";

        try {
            //Define usuário
            e = wait.element(chrome.getChrome(), By.cssSelector("input[name=\"Log\"]"));
            if (e != null) {
                e.sendKeys(user);

                //Define Senha
                e = wait.element(chrome.getChrome(), By.cssSelector("input[name=\"Sen\"]"));
                if (e != null) {
                    e.sendKeys(senha);

                    //Clica no enter
                    e = wait.element(chrome.getChrome(), By.cssSelector("form[name=\"Frm\"] input[type=\"submit\"]:nth-child(12)"));
                    if (e != null) {
                        e.click();

                        return "";
                    } else {
                        return "Input de senha não encontrado!";
                    }
                } else {
                    return "Input de senha não encontrado!";
                }
            } else {
                return "Input de login não encontrado!";
            }
        } catch (Exception e) {
            return "Erro desconhecido ao logar! Erro: " + e;
        }
    }

    public String pegarValoresNcms() {
        int max = ncms.size();

        //Inicia carregamento
        Carregamento carregamento = new Carregamento();
        carregamento.setVisible(true);
        Carregamento.barra.setMinimum(0);
        Carregamento.barra.setMaximum(max - 1);
        Carregamento.texto.setText("Iniciando...");

        //Percorre todos ncsms
        for (int i = 0; i < max; i++) {
            NcmSefaz ncm = ncms.get(i);

            /*Carregamento*/
            Carregamento.barra.setValue(i);
            Carregamento.texto.setText("NCM: " + ncm.getNcm() + " -- " + i + " de " + ncms.size());

            setCST(ncm);
            setICMS(ncm);
        }

        carregamento.dispose();

        return "";
    }

    public String getNcmsAsText(List<NcmSefaz> ncmsList, String cst_ou_icms) {
        StringBuilder retorno = new StringBuilder("NCM;Descrição;"
                + (cst_ou_icms.equals("cst")
                ? "OBS;CST PIS/COFINS"
                : "Aliq ICMS;MVA Original;MVA 12%;MVA 4%;Estados")
        );

        for (int i = 0; i < ncmsList.size(); i++) {
            NcmSefaz ncm = ncmsList.get(i);
            retorno.append("\n"
                    + ncm.getNcm() + ";" + ncm.getDescricao() + ";"
                    //Se for cst
                    + (cst_ou_icms.equals("cst")
                    //Obs e valor cst
                    ? ncm.getObs() + ";" + ncm.getCst()
                    //Valore ICMS
                    : ncm.getAliquota() + ";"
                    + ncm.getMvaOriginal() + ";"
                    + ncm.getMva12() + ";"
                    + ncm.getMva4() + ";"
                    + ncm.getEstados())
            );
        }

        return retorno.toString();
    }

    private void setCST(NcmSefaz ncm) {
        String cssCodigoNcm = "form#form_dados_ncm td:nth-child(2) > label";
        String cssDescricao = "form#form_dados_ncm td:nth-child(3) > label";
        
        String cssInputOption = "input[name='form\\[ncm\\]']";
        String cssInputTr  = "#form_dados_ncm > table > tbody > tr:nth-child(2)";
        String cssPis = "div#abas_internas div.TabbedPanelsContent.TabbedPanelsContentVisible > div.fixa2 > table > tbody > tr:nth-child(5) > td:nth-child(2)";
        String cssCofins = "div#abas_internas div.TabbedPanelsContent.TabbedPanelsContentVisible > div.fixa2 > table > tbody > tr:nth-child(5) > td:nth-child(3)";
        String cssObservacao = "div#abas_internas div.TabbedPanelsContent.TabbedPanelsContentVisible > div.relativa > table:nth-child(1) > tbody > tr:nth-child(2) > td";

        //Link Site
        String formValuesBase64 = "form%5Bncm%5D=" + ncm.getNcmFormatado() + "&form%5Bpalavra_chave%5D=&form%5Btipo_busca%5D=ncm&form%5Bacao%5D=pesquisar";
        formValuesBase64 = Base64.getEncoder().encodeToString(formValuesBase64.getBytes());
        String url = "http://www.econeteditora.com.br/pis_cofins/pis_cofins.php?i=" + formValuesBase64 + "&form[acao]=pesquisar";

        /*PIS e COFINS*/
        try {
            //Acessa site
            chrome.getChrome().get(url);
            
            //Conta com JS quantos ncms existem
            JavascriptExecutor js = (JavascriptExecutor) chrome.getChrome();
            Long nroNcmsExistentes = (long) 0;
            try {
                nroNcmsExistentes = (Long) js.executeScript("return document.querySelectorAll(\"" + cssInputOption + "\").length");
            } catch (Exception e) {
            }

            //Se tiver pelo menos uma opção
            if (nroNcmsExistentes > 0) {
                for (int i = 1; i <= nroNcmsExistentes; i++) {
                        //Entra no site
                        chrome.getChrome().get(url);

                        //Exclui linhas anteriores
                        for (int j = 1; j < i; j++) {
                            Object r = js.executeScript("return document.querySelector(\"" + cssInputTr + "\").remove()");
                        }
                        
                        //Cria NCM
                        ncmsCST.add(new NcmSefaz(chrome.getChrome().findElement(By.cssSelector(cssCodigoNcm)).getText()));
                        NcmSefaz ncmCstNovo = ncmsCST.get(ncmsCST.size()-1);
                        String descricao = chrome.getChrome().findElement(By.cssSelector(cssDescricao)).getText();
                        ncmCstNovo.setDescricao(descricao);
                        //padrao
                        ncmCstNovo.setCst("04");
                        
                        //Pega primeira opção e clica
                        e = chrome.getChrome().findElement(By.cssSelector(cssInputOption));
                        e.click();
                        
                        //Espera aparecer informações
                        e = wait.element(chrome.getChrome(), By.cssSelector(cssPis));
                        if (e != null) {
                            String pis = e.getText();
                            String cofins = chrome.getChrome().findElement(By.cssSelector(cssCofins)).getText();
                            
                            if("1,65%".equals(pis) && "7,60%".equals(cofins)){
                                ncmCstNovo.setCst("01");
                            }
                            
                            String obs = "";
                            try {
                                obs = chrome.getChrome().findElement(By.cssSelector(cssObservacao)).getText().replaceAll("\n", " ");
                            } catch (Exception e) {
                            }
                            
                            ncmCstNovo.setObs(obs);
                        }
                }
            }else{
                ncmsCST.add(new NcmSefaz(ncm.getNcm()));
                ncmsCST.get(ncmsCST.size()-1).setObs("Não cadastrado");
            }
        } catch (Exception e) {
        }
    }

    private void setICMS(NcmSefaz ncm) {
        String uf = "RS";
        String urlPesquisa = "http://www.econeteditora.com.br/icms_st/index.php?form%5Buf_origem%5D=AC&"
                + "form%5Buf%5D=" + uf + "&"
                + "form%5Bncm%5D=" + ncm.getNcm().substring(0, 4) + "&"
                + "form%5Bcest%5D=&form%5Bpalavra%5D=&acao=Buscar";
        String cssNCMOpcoes = "tr.textos input";

        String cssCodigoNCM = "div#TabbedPanels1 div.TabbedPanelsContent.TabbedPanelsContentVisible > table:nth-child(3) > tbody > tr.textos > td:nth-child(1)";
        String cssDescTribut = "div#TabbedPanels1 div.TabbedPanelsContent.TabbedPanelsContentVisible > table:nth-child(3) > tbody > tr.textos > td:nth-child(2)";
        String cssMvaOriginal = "div#TabbedPanels1 table:nth-child(5) > tbody > tr.textos > td:nth-child(1)";
        String cssMva12 = "div#TabbedPanels1 table:nth-child(5) > tbody > tr.textos > td:nth-child(3)";
        String cssMva4 = "div#TabbedPanels1 table:nth-child(5) > tbody > tr.textos > td:nth-child(2)";
        String cssAliquota = "div#TabbedPanels1 tr.textos > td:nth-child(4)";

        String cssProtocoloNome1 = "div#TabbedPanels1 td > div:nth-child(2) > table > tbody > tr:nth-child(2) > td:nth-child(1)";
        String cssProtocoloNome2 = "div#TabbedPanels1 table:nth-child(10) > tbody > tr.textos > td:nth-child(1)";
        String cssProtocoloUFs1 = "div#TabbedPanels1 td > div:nth-child(2) > table > tbody > tr:nth-child(2) > td:nth-child(2)";
        String cssProtocoloUFs2 = "div#TabbedPanels1 table:nth-child(10) > tbody > tr.textos > td:nth-child(2)";
        
        String cssProtocoloNome3 = "div#TabbedPanels1 table:nth-child(11) > tbody > tr.textos > td:nth-child(1)";
        String cssProtocoloUFs3 = "div#TabbedPanels1 table:nth-child(11) > tbody > tr.textos > td:nth-child(2)";

        try {
            //Acessa site
            chrome.getChrome().get(urlPesquisa);

            //Pega resultados
            try {
                //Conta com JS quantos ncms existem
                JavascriptExecutor js = (JavascriptExecutor) chrome.getChrome();
                Long nroNcmsExistentes = (long) 0;
                try {
                    nroNcmsExistentes = (Long) js.executeScript("return document.querySelectorAll(\"tr.textos\").length");
                } catch (Exception e) {
                }

                if (nroNcmsExistentes > 0) {

                    //Percorre todos ncms:
                    for (int i = 1; i <= nroNcmsExistentes; i++) {

                        //Entra no site
                        chrome.getChrome().get(urlPesquisa);

                        //Exclui linhas anteriores
                        for (int j = 1; j < i; j++) {
                            Object r = js.executeScript("return document.querySelector(\"tr.textos\").remove()");
                        }

                        //Pega primeira opção e clica
                        e = chrome.getChrome().findElement(By.cssSelector(cssNCMOpcoes));
                        e.click();

                        //Espera elemento Codigo NCM
                        e = wait.element(chrome.getChrome(), By.cssSelector(cssCodigoNCM));
                        if (e != null) {
                            //Define novo ncm
                            ncmsICMS.add(new NcmSefaz(e.getText()));
                            NcmSefaz ncmIcmsNovo = ncmsICMS.get(ncmsICMS.size()-1);

                            //Por padrão deixa "não se aplica"
                            setICMS_NaoSeAplica(ncmIcmsNovo);

                            try {
                                ncmIcmsNovo.setDescricao(chrome.getChrome().findElement(By.cssSelector(cssDescTribut)).getText());
                                ncmIcmsNovo.setAliquota(chrome.getChrome().findElement(By.cssSelector(cssAliquota)).getText());
                                ncmIcmsNovo.setMvaOriginal(chrome.getChrome().findElement(By.cssSelector(cssMvaOriginal)).getText());
                                ncmIcmsNovo.setMva12(chrome.getChrome().findElement(By.cssSelector(cssMva12)).getText());
                                ncmIcmsNovo.setMva4(chrome.getChrome().findElement(By.cssSelector(cssMva4)).getText());

                                /*Set Estados*/
                                try {
                                    ncmIcmsNovo.addInEstados(chrome.getChrome().findElement(By.cssSelector(cssProtocoloNome1)).getText());
                                    ncmIcmsNovo.addInEstados(" (" + chrome.getChrome().findElement(By.cssSelector(cssProtocoloUFs1)).getText() + ") ");
                                    
                                    ncmIcmsNovo.addInEstados(chrome.getChrome().findElement(By.cssSelector(cssProtocoloNome2)).getText());
                                    ncmIcmsNovo.addInEstados(" (" + chrome.getChrome().findElement(By.cssSelector(cssProtocoloUFs2)).getText() + ") ");
                                } catch (Exception e) {
                                    if(ncmIcmsNovo.getEstados().equals("")){
                                        ncmIcmsNovo.addInEstados(chrome.getChrome().findElement(By.cssSelector(cssProtocoloNome3)).getText());
                                        ncmIcmsNovo.addInEstados(" (" + chrome.getChrome().findElement(By.cssSelector(cssProtocoloUFs3)).getText() + ") ");
                                    }
                                }

                            } catch (Exception e) {

                            }
                        } else {
                            //Não possui Código ncm nessa pagina (Provavelmente erro e nunca vai passar aqui)
                        }
                    }
                } else {
                    ncmsICMS.add(new NcmSefaz(ncm.getNcm()));
                    setICMS_NaoSeAplica(ncmsICMS.get(ncmsICMS.size()-1));
                }
            } catch (Exception e) {
                ncmsICMS.add(new NcmSefaz(ncm.getNcm()));
                setICMS_NaoSeAplica(ncmsICMS.get(ncmsICMS.size()-1));
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private void setICMS_NaoSeAplica(NcmSefaz ncm) {
        ncm.setAliquota("Não se Aplica");
        ncm.setMvaOriginal("Não se Aplica");
        ncm.setMva12("Não se Aplica");
        ncm.setMva4("Não se Aplica");
    }

    public List<NcmSefaz> getNcmsCST() {
        return ncmsCST;
    }

    public List<NcmSefaz> getNcmsICMS() {
        return ncmsICMS;
    }

}
