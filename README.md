# Projeto Pokédex Android (MVVM + Compose)

Aplicativo Android nativo desenvolvido como projeto acadêmico da disciplina de Desenvolvimento de Aplicativos Móveis. O aplicativo permite aos usuários explorar uma lista completa de Pokémon, visualizar detalhes de cada um e montar um time personalizado de até 6 Pokémon.

O projeto implementa as melhores práticas de desenvolvimento Android, incluindo arquitetura MVVM, Jetpack Compose para UI declarativa, Room para persistência local, e Retrofit para consumo de APIs REST.

## Funcionalidades

### Pokédex Completa
* Lista paginada de todos os Pokémon da PokeAPI
* Visualização em grid com cards coloridos por tipo
* Busca por nome ou número do Pokémon
* Carregamento infinito (scroll infinito)
* Cache local para funcionamento offline

### Detalhes do Pokémon
* Informações completas: tipos, altura, peso
* Estatísticas base animadas (HP, Ataque, Defesa, Atq. Especial, Def. Especial, Velocidade)
* Sprites oficiais de alta qualidade
* Design adaptativo com cores baseadas no tipo

### Gerenciamento de Time
* Adicionar Pokémon ao time pessoal (máximo 6)
* Visualizar time montado em tela dedicada
* Remover Pokémon individualmente
* Limpar todo o time de uma vez
* Persistência local do time

### Interface Moderna
* Design Material 3
* Animações suaves e intuitivas
* Navegação fluida entre telas
* Tema com gradientes e efeitos visuais
* Responsivo e otimizado

## Tecnologias Utilizadas

### Core
* Kotlin 1.9.21
* Android SDK - API Level 24+ (Android 7.0+)

### Arquitetura e Padrões
* MVVM (Model-View-ViewModel)
* Clean Architecture
* Repository Pattern
* Dependency Injection - Hilt

### Bibliotecas Principais

| Biblioteca | Versão | Uso |
| :--- | :--- | :--- |
| Jetpack Compose | 2024.02.00 | UI declarativa |
| Material 3 | latest | Design system |
| Room | 2.6.1 | Banco de dados local |
| Retrofit | 2.9.0 | Cliente HTTP |
| Gson | 2.9.0 | Serialização JSON |
| Hilt | 2.50 | Injeção de dependência |
| Coil | 2.5.0 | Carregamento de imagens |
| Coroutines | 1.7.3 | Programação assíncrona |
| Navigation Compose | 2.7.6 | Navegação |
| ViewModel | 2.7.0 | Gerenciamento de estado |

## API Externa

* **PokeAPI (https://pokeapi.co/)** - API REST pública com dados de todos os Pokémon.

## Arquitetura

O projeto segue a arquitetura MVVM com Clean Architecture, separado em 3 camadas principais:
````
┌─────────────────────────────────────────────┐
│            UI LAYER (Compose)               │
│   • Screens (PokemonList, Details, Team)    │
│   • ViewModels                              │
│   • Theme & Components                      │
└──────────────────┬──────────────────────────┘
                   │ observa StateFlow
                   ▼
┌─────────────────────────────────────────────┐
│         DOMAIN LAYER (Business)             │
│   • Models (Pokemon, TeamMember)            │
│   • Use Cases (opcional)                    │
└──────────────────┬──────────────────────────┘
                   │ fornece dados
                   ▼
┌─────────────────────────────────────────────┐
│          DATA LAYER (Repositories)          │
│   ┌─────────────┴─────────────┐             │
│   ▼                           ▼             │
│  Room (Local)            Retrofit (Remote)  │
│  • Entities              • DTOs             │
│  • DAOs                  • API Service      │
└─────────────────────────────────────────────┘
````

## Estratégia de Cache

* **Offline First**: Dados são salvos localmente e exibidos imediatamente.
* **Cache Inteligente**: Reduz chamadas de rede desnecessárias.
* **Sincronização**: Atualiza cache em segundo plano quando online.

## Como Executar

### Pré-requisitos
* Android Studio Hedgehog (2023.1.1) ou superior
* JDK 17 ou superior
* Emulador Android ou Dispositivo físico com Android 7.0+ (API 24+)
* Conexão com internet (para primeira execução)

## Participantes

* Pedro Miguel Radwanski - 8132778953
* John Claude Cameron Chappell - 8133085404
* Felipe de Mello Vieira - 8132601158
* Gustavo Rossi Silva - 8140871584
