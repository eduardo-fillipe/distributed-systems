package br.ufs.dcomp.eduard6.ds.car;

import br.ufs.dcomp.eduard6.ds.car.gps.RandomicCarGPS;

import java.util.Scanner;

/**
 * Interface de console para um carro.
 */
public class CarConsoleInterface {
    private final Scanner scanner = new Scanner(System.in);
    private final Car car;

    public CarConsoleInterface() {
        String id = readId();
        this.car = new Car(id, new RandomicCarGPS());

        show(String.format("ID INSERIDA: '%s'. PRESSIONE ENTER PARA CONTINUAR E SE CONECTAR AO SERVIÇO...", id));
        scanner.nextLine();

        showMenu();
    }

    private void showMenu() {
        int op = 0;
        while (op != 9) {
            show(String.format("=========================================== MENU DO CARRO %s =========================================\n", this.car.getId()));
            show("ESCOLHA A OPÇÃO DESEJADA:\n");
            show("\t( 1 ) ENVIAR MENSAGEM\n");
            show("\t( 2 ) ENVIAR ALERTA\n");
            show("\t( 3 ) INFORMAR PARADA\n");
            show("\t( 9 ) ENCERRAR\n");

            op = readInt();
            switch (op) {
                case 1:
                    showSendMessage();
                    break;
                case 2:
                    showSendAlert();
                    break;
                case 3:
                    showNotifyStop();
            }
        }
        endApplication();
    }

    private void showNotifyStop() {
        show("============================================== NOTIFICAR PARADA =============================================\n");
        show("\tNOTIFICANDO PARADA OBRIGATÓRIO A PARTIR DOS DADOS DO GPS...\n\n");
        if (car.notifyStop()) {
          show("\tPARADA OBRIGATÓRIA NOTIFICADA COM SUCESSO!\n");
        } else {
            show("\tERRO DURANTE A NOTIFICAÇÃO DE PARADA OBRIGATÓRIA!\n");
        }
        show("PRESSIONE QUALQUER TECLA PARA VOLTAR...\n");
        scanner.nextLine();

    }

    private void showSendAlert(){
        show("================================================ ENVIAR ALERTA ===============================================\n");
        show("\tDIGITE A MENSAGEM QUE SERÁ ENVIADA NO ALERTA: ");
        String text = scanner.nextLine();
        while (text.isBlank())
            text = scanner.nextLine();

        show("\n");

        if (car.sendAlert(text)) {
            show("\tALERTA ENVIADO COM SUCESSO! DIGITE ENTER PARA CONTINUAR...");
            scanner.nextLine();
        }
    }

    private void showSendMessage() {
        show("========================================== ENVIAR MENSAGEM PARA CARRO ========================================\n");
        show("DIGITE O ID DO VEÍCULO QUE RECEBERÁ A MENSAGEM('CENTRAL' SE O ALVO FOR A CENTRAL): ");
        String id = scanner.nextLine();
        while (id.isBlank())
            id = scanner.nextLine();

        show("\tDIGITE A MENSAGEM QUE SERÁ ENVIADA: ");
        String text = scanner.nextLine();
        while (text.isBlank())
            text = scanner.nextLine();

        show("\n");

        if (car.sendMessage(id, text)) {
            show("MENSAGEM ENVIADA COM SUCESSO! DIGITE ENTER PARA CONTINUAR...");
            scanner.nextLine();
        }
    }

    private void endApplication() {
        try {
            show("ENCERRANDO APLICAÇÃO...");
            car.shutdown();
            scanner.close();
        } catch (Exception e) {
            show(String.format("ERRO DURANTE O ENCERRAMENTO DA APLICAÇÃO: %s", e.getMessage()));
        }
    }

    private String readId() {
        String newName;
        show("DIGITE SEU IDENTIFICADOR: \n");
        while ((newName = scanner.nextLine()).isBlank());
        return newName;
    }

    public void show(String str) {
        System.out.print(str);
    }

    public int readInt() {
        int result = scanner.nextInt();
        scanner.nextLine();
        return result;
    }

    private void clearConsole() {
        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                Runtime.getRuntime().exec("cls");
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e)
        {
            //  Handle any exceptions.
        }
    }

    public static void main(String[] args) {
        new CarConsoleInterface();
    }
}
