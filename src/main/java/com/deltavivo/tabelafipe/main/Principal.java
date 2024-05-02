package com.deltavivo.tabelafipe.main;

import com.deltavivo.tabelafipe.model.Dados;
import com.deltavivo.tabelafipe.model.Modelos;
import com.deltavivo.tabelafipe.model.Veiculo;
import com.deltavivo.tabelafipe.service.ConsumoAPI;
import com.deltavivo.tabelafipe.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);

    private ConsumoAPI consumo = new ConsumoAPI();

    private ConverteDados conversor = new ConverteDados();

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu(){

        var menu = """
                -------------------------------------------
                *** Opções ***
                -------------------------------------------
                Carro
                Moto
                Caminhão
                -------------------------------------------
                
                Digite uma das opções para consultar:
                """;

        System.out.printf(menu);
        var opcao = leitura.nextLine();

        String endereco;

        if (opcao.toLowerCase().contains("carr")){
            endereco = URL_BASE + "carros/marcas";
        } else if (opcao.toLowerCase().contains("mot")){
            endereco = URL_BASE + "motos/marcas";
        } else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        // Tratamento do json por tipo de veiculo

        var json = consumo.obterDados(endereco);
        System.out.printf(json);
        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.printf("Informe o codigo da marca para consulta:");
        var codigoMarca = leitura.nextLine();

        //Tratamento do json por marca escolhida
        //URL_BASE + "carros/marcas/" + codigoMarca + "/modelos";
        endereco = endereco + "/" + codigoMarca + "/modelos";

        json = consumo.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.printf("\nModelos dessa marca: ");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        // Tratamento do json por modelo escolhido
        // URL_BASE + "carros/marcas/" + codigoMarca + "/modelos/" + ano + "/anos" ;

        System.out.printf("\n Digite um trecho do nome do carro a ser buscado: \n");
        var nomeVeiculo = leitura.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados: ");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("\nDigite o codigo do modelo escolhido para buscar os valores de avaliaçao: ");
        var codigoModelo = leitura.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumo.obterDados(endereco);

        List<Dados> anos = conversor.obterLista(json, Dados.class);

        List<Veiculo> veiculos = new ArrayList<>();

        for(int i = 0; i < anos.size(); i++){
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.printf("\nTodos os veiculos filtrados com avaliacao por ano: \n");
        veiculos.forEach(System.out::println);

    }
}
