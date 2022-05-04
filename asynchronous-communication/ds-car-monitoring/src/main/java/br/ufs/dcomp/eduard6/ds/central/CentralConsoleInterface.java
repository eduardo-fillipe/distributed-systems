package br.ufs.dcomp.eduard6.ds.central;

import br.ufs.dcomp.eduard6.ds.to.ObrigatoryStopEvent;
import br.ufs.dcomp.eduard6.ds.to.UpdateCarInfoEvent;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console para uma central
 *
 */
public class CentralConsoleInterface {
    private final Scanner scanner = new Scanner(System.in);
    private final MonitoringCentral monitoringCentral;

    public CentralConsoleInterface(MonitoringCentral monitoringCentral) {
        this.monitoringCentral = monitoringCentral;
    }

    public void showMenu() {
        int op = 0;
        while (op != 9) {
            show("=============================================== MENU DA CENTRAL =============================================\n");
            show("ESCOLHA A OPÇÃO DESEJADA:\n");
            show("\t( 1 ) EXIBIR CARROS CONECTADOS\n");
            show("\t( 2 ) EXIBIR INFORMAÇÕES DE CARROS\n");
            show("\t( 3 ) ENVIAR ALERTA PARA CARROS\n");
            show("\t( 4 ) ENVIAR MENSAGEM PARA CARRO\n");
            show("\t( 9 ) ENCERRAR\n");

            op = readInt();
            switch (op) {
                case 1:
                    showConnected();
                    break;
                case 2:
                    showCarsInfo();
                    break;
                case 3:
                    showSendAlert();
                    break;
                case 4:
                    showSendMessage();
                    break;
            }
        }
        endApplication();
    }

    private void showSendMessage() {
        show("========================================== ENVIAR MENSAGEM PARA CARRO ========================================\n");
        show("DIGITE O ID DO VEÍCULO QUE RECEBERÁ A MENSAGEM: ");
        String id = scanner.nextLine();
        while (id.isBlank())
            id = scanner.nextLine();

        show("\bDIGITE A MENSAGEM QUE SERÁ ENVIADA: ");
        String text = scanner.nextLine();
        while (text.isBlank())
            text = scanner.nextLine();

        show("\n");

        if (monitoringCentral.sendMessage(id, text)) {
            show("MENSAGEM ENVIADA COM SUCESSO! DIGITE ENTER PARA CONTINUAR...");
            scanner.nextLine();
        }
    }


    private void showSendAlert() {
        String alertText = "";
        while (!alertText.equalsIgnoreCase("-")) {
            show("==================================== ENVIAR ALERTA PARA TODOS OS CARROS ======================================\n");
            show("DIGITE O TEXTO DO ALERTA(DIGITE '-' PARA SAIR):\n");
            alertText = scanner.nextLine();
            while (alertText.isBlank())
                alertText = scanner.nextLine();

            monitoringCentral.alertVehicles(alertText);
            show("\t ALERTA ENVIADO!\n");

            show("DIGITE O TEXTO DO ALERTA(DIGITE '-' PARA SAIR):\n");
            alertText = scanner.nextLine();
        }
    }

    private void showCarsInfo() {
        int op = 0;
        while (op != 3) {
            show("============================================ EXIBIR INFORMAÇÕES ==============================================\n");
            show("DIGITE A OPÇÃO DESEJADA:\n");
            show("\t( 1 ) LOCAL E VELOCIDADE\n");
            show("\t( 2 ) PARADAS PROGRAMADAS REALIZADAS\n");
            show("\t( 3 ) VOLTAR\n");

            op = readInt();
            switch (op) {
                case 1:
                    showCarsCords();
                    break;
                case 2:
                    showCarsStops();
                    break;
            }
        }
    }

    private void showCarsStops() {
        show("================================= EXIBIR INFORMAÇÕES: PARADAS OBRIGATÓRIAS ===================================\n");
        show("DIGITE A PLACA DO CARRO(Digite '-' para voltar):\n");
        String placa = scanner.nextLine();

        while (!"-".equalsIgnoreCase(placa)) {
            if (!placa.isBlank()) {
                Optional<List<ObrigatoryStopEvent>> evt = monitoringCentral.getObrigatoryStops(placa);
                if (evt.isEmpty()) {
                    show("\tESTE VEÍCULO AINDA NÃO FEZ PARADAS OU NÃO SE CONECTOU!\n");
                } else {
                    show("\tParadas de "+ placa + ": "+ evt.get() + "\n");
                }
            }
            show("DIGITE A PLACA DO CARRO(Digite '-' para voltar):\n");
            placa = scanner.nextLine();
        }
    }

    private void showCarsCords() {
        show("================================== EXIBIR INFORMAÇÕES: LOCAL E VELOCIDADE ====================================\n");
        show("DIGITE A PLACA DO CARRO OU PRESSIONE ENTER PARA LISTAR TODOS ('-' para voltar):\n");
        String placa = scanner.nextLine();

        while (!"-".equalsIgnoreCase(placa)) {
            if (placa.isBlank()) {
                show("\t" + monitoringCentral.getCarsInfo() + "\n");
            } else {
                Optional<UpdateCarInfoEvent> evt = monitoringCentral.getCarInfo(placa);
                if (evt.isEmpty()) {
                    show("\tPLACA NÃO LOCALIZADA. ESTE VEÍCULO AINDA NÃO SE IDENTIFICOU!\n");
                } else {
                    show("\t" + evt.get() + "\n");
                }
            }
            show("DIGITE A PLACA DO CARRO OU PRESSIONE ENTER PARA LISTAR TODOS ('-' para voltar):\n");
            placa = scanner.nextLine();
        }
    }

    private void showConnected() {
        show("================================================ CARROS ATIVOS ==============================================\n");
        show("LISTA DE CARROS ATIVOS:\n");
        show("\t" + monitoringCentral.getActiveCars() + "\n");
        show("DIGITE ENTER PARA VOLTAR...\n");
        scanner.nextLine();
    }

    private void endApplication() {
        try {
            show("ENCERRANDO APLICAÇÃO...");
            monitoringCentral.close();
            scanner.close();
        } catch (Exception e) {
            show(String.format("ERRO DURANTE O ENCERRAMENTO DA APLICAÇÃO: %s", e.getMessage()));
        }
    }

    public void show(String str){
        System.out.print(str);
    }

    public int readInt() {
        int result = scanner.nextInt();
        scanner.nextLine();
        return result;
    }

    public static void main(String[] args) {
        new CentralConsoleInterface(new MonitoringCentral()).showMenu();
    }
}
