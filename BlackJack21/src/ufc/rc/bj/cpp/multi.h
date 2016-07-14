#ifndef MULTI_H
#define MULTI_H
#include <iostream>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <unistd.h>
#include <vector>
#include <stdio.h>
#include <stdlib.h>
#include <fstream>
#include <math.h>
#include <thread>

using namespace std;

void iniciaBaralho(vector<string> &baralho);
void gerarCarta(char *carta, vector<string> &baralho);
void novoJogo(int SckCliente);
int faznada(int t);

#endif // MULTI_H