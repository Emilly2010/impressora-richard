import java.util.Scanner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InputMismatchException;

public class Impressora {

    static boolean conexaoAberta = false;
    static boolean conexaoConfigurada = false;
    static int tipo;
    static String modelo;
    static String conexao;
    static int parametro;
    static Scanner input = new Scanner(System.in);

    static int lerInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);

            try {
                int numero = input.nextInt();
                input.nextLine();
                return numero;

            } catch (InputMismatchException e) {
                System.out.println("Digite apenas numeros!");
                input.nextLine();
            }
        }
    }

    void configurarImpressora() {

        tipo = lerInteiro("Digite o tipo de conexao (1 = USB, 2 = Serial, 3 = Rede, 4 = Bluetooth): ");

        while (tipo < 1 || tipo > 4) {
            System.out.println("Tipo de conexao invalido! Digite um numero de 1 a 4.");
            tipo = lerInteiro("Digite novamente: ");
        }

        System.out.println("Digite o modelo da impressora (i8 ou i9): ");
        modelo = input.nextLine();

        System.out.println("Digite a conexao (USB, COM2 ou IP): ");
        conexao = input.nextLine();

        parametro = lerInteiro("Digite o parametro (0 para USB, velocidade para Serial ou porta para Rede): ");

        conexaoConfigurada = true;

        System.out.println("Dados armazenados com sucesso!");
    }

    void abrirConexao() {

        if (conexaoConfigurada == false) {
            System.out.println("Configure a impressora antes de abrir a conexao!");
            return;
        }

        if (conexaoAberta == false) {

            try {
                int ret = ImpressoraDLL.INSTANCE.AbreConexaoImpressora(tipo, modelo, conexao, parametro);

                if (ret == 0) {
                    conexaoAberta = true;
                    System.out.println("Comunicou com sucesso!");
                } else {
                    System.out.println("Erro ao abrir conexao. Retorno " + ret);
                }

            } catch (LinkageError e) {
                System.out.println("Erro ao carregar a DLL da impressora: " + e.getMessage());
            }

        } else {
            System.out.println("Ja existe uma comunicacao em aberto");
        }
    }

    void impressaoCodigoBarras() {

        if (conexaoAberta == false) {
            System.out.println("Conexao fechada. Abra a conexao antes.");
            return;
        }
        
        String dados = "{A012345678912";

        int ret = ImpressoraDLL.INSTANCE.ImpressaoCodigoBarras(8, dados, 100, 2, 1);

        if (ret == 0) {
            System.out.println("Codigo de barras impresso com sucesso!");

            int corte = ImpressoraDLL.INSTANCE.Corte(3);

            if (corte == 0) {
                System.out.println("Corte realizado com sucesso!");
            } else {
                System.out.println("Erro ao realizar o corte. Retorno: " + corte);
            }

        } else {
            System.out.println("Erro ao imprimir codigo de barras. Retorno: " + ret);
        }
    }

    void fecharConexao() {

        if (conexaoAberta == true) {

            int ret = ImpressoraDLL.INSTANCE.FechaConexaoImpressora();

            if (ret == 0) {
                conexaoAberta = false;
                System.out.println("Conexao fechada com sucesso!");
            } else {
                System.out.println("Erro ao fechar conexao. Retorno: " + ret);
            }

        } else {
            System.out.println("Nao existe uma conexao aberta.");
        }
    }

    void impressaoTexto() {

        if (conexaoAberta == true) {

            System.out.println("Digite o texto que deseja imprimir:");
            String dados = input.nextLine();

            int copias = lerInteiro("Quantidade de copias: ");

            if (copias <= 0) {
                System.out.println("Precisa ser maior que zero!");
                return;
            }

            for (int i = 1; i <= copias; i++) {

                int ret = ImpressoraDLL.INSTANCE.ImpressaoTexto(dados, 0, 0, 0);

                if (ret >= 0) {
                    System.out.println("Texto enviado com sucesso! Copia " + i);

                    int corte = ImpressoraDLL.INSTANCE.Corte(3);

                    if (corte != 0) {
                        System.out.println("Erro ao cortar papel. Retorno: " + corte);
                    }

                } else {
                    System.out.println("Erro ao imprimir texto. Retorno: " + ret);
                }
            }

        } else {
            System.out.println("Abra a conexao antes de imprimir.");
        }
    }

    void impressaoQRCode() {

        if (conexaoAberta == true) {

            System.out.println("Digite o conteudo do QR Code:");
            String dados = input.nextLine();

            int ret = ImpressoraDLL.INSTANCE.ImpressaoQRCode(dados, 4, 2);

            if (ret == 0) {
                System.out.println("QR Code enviado para impressao!");
            } else {
                System.out.println("Erro ao imprimir QR Code. Retorno: " + ret);
            }

        } else {
            System.out.println("Abra a conexao antes de imprimir.");
        }
    }

    void imprimirXMLSAT() {

        if (conexaoAberta == true) {

            try {
                String dados = Files.readString(Paths.get("XMLSAT.xml"));

                int ret = ImpressoraDLL.INSTANCE.ImprimeXMLSAT(dados, 0);

                if (ret == 0) {
                    System.out.println("XML SAT enviado para impressao!");
                } else {
                    System.out.println("Erro ao imprimir XML SAT. Retorno: " + ret);
                }

            } catch (IOException e) {
                System.out.println("Erro ao encontrar ou ler o arquivo XML SAT.");
            }

        } else {
            System.out.println("Abra a conexao antes de imprimir.");
        }
    }

    void imprimirXMLCancelamento() {

        if (conexaoAberta == true) {

            try {
                String dados = Files.readString(Paths.get("CANC_SAT.xml"));

                String assinatura = "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";

                int ret = ImpressoraDLL.INSTANCE.ImprimeXMLCancelamentoSAT(dados, assinatura, 0);

                if (ret == 0) {
                    System.out.println("XML de cancelamento enviado para impressao!");
                } else {
                    System.out.println("Erro ao imprimir XML de cancelamento. Retorno: " + ret);
                }

            } catch (IOException e) {
                System.out.println("Erro ao encontrar ou ler o arquivo XML de cancelamento.");
            }

        } else {
            System.out.println("Abra a conexao antes de imprimir.");
        }
    }

    void abrirGaveta() {

        if (conexaoAberta == true) {

            int ret = ImpressoraDLL.INSTANCE.AbreGavetaElgin(0, 1, 5);

            if (ret == 0) {
                System.out.println("Comando para abrir a gaveta enviado!");
            } else {
                System.out.println("Erro ao abrir gaveta. Retorno: " + ret);
            }

        } else {
            System.out.println("Abra a conexao antes de utilizar a gaveta.");
        }
    }

    void sinalSonoro() {

        if (conexaoAberta == true) {

            int ret = ImpressoraDLL.INSTANCE.SinalSonoro(2, 50, 5);

            if (ret == 0) {
                System.out.println("Sinal sonoro enviado!");
            } else {
                System.out.println("Erro no sinal sonoro. Retorno: " + ret);
            }

        } else {
            System.out.println("Abra a conexao antes de utilizar o sinal sonoro.");
        }
    }

    void avancarPapel() {

        if (conexaoAberta == true) {

            int linhas = lerInteiro("Quantas linhas deseja avancar? ");

            int ret = ImpressoraDLL.INSTANCE.AvancaPapel(linhas);

            if (ret == 0) {
                System.out.println("Papel avancado com sucesso!");
            } else {
                System.out.println("Erro ao avancar papel. Retorno: " + ret);
            }

        } else {
            System.out.println("Abra a conexao antes de avancar o papel.");
        }
    }

    void statusPapel() {

        if (conexaoAberta == true) {

            int ret = ImpressoraDLL.INSTANCE.StatusImpressora(3);

            if (ret == 5) {
                System.out.println("A impressora possui papel.");
            } else if (ret == 6) {
                System.out.println("O papel esta acabando.");
            } else if (ret == 7) {
                System.out.println("A impressora esta sem papel.");
            } else {
                System.out.println("Retorno do status: " + ret);
            }

        } else {
            System.out.println("Abra a conexao antes de consultar o status.");
        }
    }
}





