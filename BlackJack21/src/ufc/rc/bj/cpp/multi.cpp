#include "multi.h"

void iniciaBaralho(vector <string> &baralho){
    baralho.push_back("As\n");
    baralho.push_back("Ad\n");
    baralho.push_back("Ac\n");
    baralho.push_back("Ah\n");

    baralho.push_back("2s\n");
    baralho.push_back("2d\n");
    baralho.push_back("2c\n");
    baralho.push_back("2h\n");

    baralho.push_back("3s\n");
    baralho.push_back("3d\n");
    baralho.push_back("3c\n");
    baralho.push_back("3h\n");

    baralho.push_back("4s\n");
    baralho.push_back("4d\n");
    baralho.push_back("4c\n");
    baralho.push_back("4h\n");

    baralho.push_back("4s\n");
    baralho.push_back("4d\n");
    baralho.push_back("4c\n");
    baralho.push_back("4h\n");

    baralho.push_back("5s\n");
    baralho.push_back("5d\n");
    baralho.push_back("5c\n");
    baralho.push_back("5h\n");

    baralho.push_back("6s\n");
    baralho.push_back("6d\n");
    baralho.push_back("6c\n");
    baralho.push_back("6h\n");

    baralho.push_back("7s\n");
    baralho.push_back("7d\n");
    baralho.push_back("7c\n");
    baralho.push_back("7h\n");

    baralho.push_back("8s\n");
    baralho.push_back("8d\n");
    baralho.push_back("8c\n");
    baralho.push_back("8h\n");

    baralho.push_back("9s\n");
    baralho.push_back("9d\n");
    baralho.push_back("9c\n");
    baralho.push_back("9h\n");

    baralho.push_back("Ts\n");
    baralho.push_back("Td\n");
    baralho.push_back("Tc\n");
    baralho.push_back("Th\n");

    baralho.push_back("Js\n");
    baralho.push_back("Jd\n");
    baralho.push_back("Jc\n");
    baralho.push_back("Jh\n");

    baralho.push_back("Qs\n");
    baralho.push_back("Qd\n");
    baralho.push_back("Qc\n");
    baralho.push_back("Qh\n");

    baralho.push_back("Ks\n");
    baralho.push_back("Kd\n");
    baralho.push_back("Kc\n");
    baralho.push_back("Kh\n");
}

void gerarCarta(char *carta, vector<string> &baralho){
        if(baralho.size() == 0)
            return;
        int n = (int)clock();
        int size = baralho.size();
        n = rand_r ((unsigned int *)&n)%size;
        string aux = baralho[n];
        cout<<aux<<endl;
        strcpy(carta+5, aux.c_str());
        baralho.erase(baralho.begin()+n);
}



int faznada(int t)
{
    return 976;
}


void novoJogo(int SckCliente)
{
    vector <string> baralho;
    char carta[10];
    iniciaBaralho(baralho);
    strcpy(carta,"BEGIN");
    void * jogada;
    int lidos, escritos,n , soma = 0;
    jogada = malloc(10 * sizeof(char));

    //Mão
    gerarCarta(carta, baralho);
    escritos = write(SckCliente,carta,9);
    if(escritos == -1)
        cerr <<"Erro no socket";
    gerarCarta(carta, baralho);

    escritos = write(SckCliente,carta,9);
    if(escritos == -1)
        cerr << "Erro no socket";

    while(1){
        lidos = read(SckCliente,jogada, 10);
        *((char *)jogada+lidos) = '\0';
        if (lidos == -1 )
            cerr << "Erro ao ler jogada (214)" << endl;

        //Pedir
        if(*((char *)jogada) == 'P'){
            gerarCarta(carta, baralho);
            escritos = write(SckCliente,carta,9);
        }

        //Parar
        if(*((char *)jogada) == 'Q'){
            soma = 0;
           while(soma < 17){
                n = (int)clock();
                soma += rand_r ((unsigned int *)&n)%13;
           }

           sprintf(&(carta[0]),"NUB%d\n",soma);
           escritos = write(SckCliente, carta, 8);
           //iniciaBaralho(baralho);
           //strcpy(carta,"BEGIN");
           close(SckCliente);
           return;
           //break;
        }
    }
}