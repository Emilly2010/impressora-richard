import java.util.Scanner;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner ler = Impressora.input;

        Usuario pessoa = new Usuario();
        boolean login = false;

        for (int tentativas = 3; tentativas > 0 && login == false; tentativas--) {
            System.out.print("Usuário: ");
            pessoa.usuario1 = ler.nextLine();

            pessoa.senha1 = Impressora.lerInteiro("Senha: ");

            login = pessoa.verificarLogin();

            if (login == false) {
                System.out.println("Erro. Tentativas restantes: " + (tentativas - 1));
            }
        }

        if (login == true) {
            System.out.println("Login feito com sucesso!");
            Menu menu = new Menu();
            menu.menuImpressora();
        } else {
            System.out.println("Tentativas esgotadas. Saindo...");
        }
    }
}
