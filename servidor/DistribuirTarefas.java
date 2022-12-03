import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class DistribuirTarefas implements Runnable {

	private Socket socket;
	private ServidorTarefas servidor;
	private BlockingQueue<String> filaComandos;

	public DistribuirTarefas(ExecutorService threadPool, BlockingQueue<String> filaComandos, Socket socket, ServidorTarefas servidor) {
		this.filaComandos = filaComandos;
		this.socket = socket;
		this.servidor = servidor;
	}

	@Override
	public void run() {

		Map<String, String> usuarios = new HashMap<String, String>();

		try {
			
			System.out.println("Distribuindo as tarefas para o cliente " + socket);

			Scanner entradaCliente = new Scanner(socket.getInputStream());

			PrintStream saidaCliente = new PrintStream(socket.getOutputStream());

			while (entradaCliente.hasNextLine()) {

				String comando = entradaCliente.nextLine();
				System.out.println("Comando recebido " + comando);

				switch (comando) {
					case "1": {						
						saidaCliente.println("Digite o e-mail: ");
		                // ComandoC1 c1 = new ComandoC1(saidaCliente);
		                // this.threadPool.submit(c1);
						String email = entradaCliente.nextLine();
						saidaCliente.println("Digite o nome: ");
						String nome = entradaCliente.nextLine();
						if(usuarios.containsKey(email) == false) {
							usuarios.put(email, nome);
							saidaCliente.println("Usuário cadastrado");
						} else {
							saidaCliente.println("E-mail já existente no sistema. Tente outro.");
						}
						
						System.out.println(usuarios);
						break;
					}
					case "2": {
						saidaCliente.println("Digite o e-mail do usuário a ser excluído: ");
						String email = entradaCliente.nextLine();
						if(usuarios.containsKey(email) == true) {
							usuarios.remove(email);
							saidaCliente.println("Usuário removido com sucesso.");
						} else {
							saidaCliente.println("Usuário não cadastrado no sistema.");
						}
						break;
					}
					case "3" : {
						saidaCliente.println(usuarios);
						break;

					}
					case "4" : {
					    this.filaComandos.put(comando); //lembrando, bloqueia se tiver cheia
					    saidaCliente.println("Uma tarefa foi adicionada no backlog.");
					    break;
					}
					case "5": {
						saidaCliente.println("Desligando o servidor");
						servidor.parar();
						return;
					}
					default: {
						saidaCliente.println("Comando não encontrado");
					}
				}

			}

			saidaCliente.close();
			entradaCliente.close();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
