#include "multi.h"

using namespace std;
int main(){

    int SckServidor, SckCliente;
    int portNumber = 65376;


    struct sockaddr_in serv_addr, cli_addr;
    socklen_t clilen;

    SckServidor = socket(AF_INET, SOCK_STREAM, 0);

    //Verificaçao de erro ao conectar
    if (SckServidor < 0){
        cerr << "Erro ao Conectar... (118)" << endl;
        exit(1);
    }

    cout << "Conectando..." << endl;

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(portNumber);

    //Verificaçao de erro ao gerar porta
    if (bind(SckServidor,(struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0){
        cerr << "Erro ao gerar porta (130)" << endl;
        exit(1);
    }
    clilen = sizeof(cli_addr);

    listen (SckServidor, 30);
    while(1){
        SckCliente = accept(SckServidor, (struct sockaddr *)&cli_addr, &clilen);
        cout<<"clienteNovo"<<endl;
        thread t(novoJogo,SckCliente);
        t.detach();
    }
    return 0;
}

