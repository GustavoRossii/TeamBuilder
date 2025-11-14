Pokédex com gerenciamento de times, desenvolvido em Kotlin com Jetpack Compose
Funcionalidades • Arquitetura • Equipe

Sobre o Projeto
O Pokédex App é um aplicativo Android nativo desenvolvido como projeto acadêmico da disciplina de Desenvolvimento de Aplicativos Móveis. O aplicativo permite aos usuários explorar uma lista completa de Pokémon, visualizar detalhes de cada um e montar um time personalizado de até 6 Pokémon.
O projeto implementa as melhores práticas de desenvolvimento Android, incluindo arquitetura MVVM, Jetpack Compose para UI declarativa, Room para persistência local, e Retrofit para consumo de APIs REST.

Funcionalidades
Pokédex Completa

Lista paginada de todos os Pokémon da PokeAPI
Visualização em grid com cards coloridos por tipo
Busca por nome ou número do Pokémon
Carregamento infinito (scroll infinito)
Cache local para funcionamento offline

Detalhes do Pokémon

Informações completas: tipos, altura, peso
Estatísticas base animadas (HP, Ataque, Defesa, Atq. Especial, Def. Especial, Velocidade)
Sprites oficiais de alta qualidade
Design adaptativo com cores baseadas no tipo

Gerenciamento de Time

Adicionar Pokémon ao time pessoal (máximo 6)
Visualizar time montado em tela dedicada
Remover Pokémon individualmente
Limpar todo o time de uma vez
Persistência local do time

Interface Moderna

Design Material 3
Animações suaves e intuitivas
Navegação fluida entre telas
Tema com gradientes e efeitos visuais
Responsivo e otimizado


Tecnologias Utilizadas
Core

Kotlin 1.9.21 - Linguagem principal
Android SDK - API Level 24+ (Android 7.0+)
Jetpack Compose - UI declarativa moderna

Arquitetura e Padrões

MVVM (Model-View-ViewModel) - Arquitetura
Clean Architecture - Separação de camadas
Repository Pattern - Abstração de dados
Dependency Injection - Hilt

Bibliotecas Principais
BibliotecaVersãoUsoJetpack Compose2024.02.00UI declarativaMaterial 3latestDesign systemRoom2.6.1Banco de dados localRetrofit2.9.0Cliente HTTPGson2.9.0Serialização JSONHilt2.50Injeção de dependênciaCoil2.5.0Carregamento de imagensCoroutines1.7.3Programação assíncronaNavigation Compose2.7.6NavegaçãoViewModel2.7.0Gerenciamento de estado
API Externa

PokeAPI - API REST pública com dados de todos os Pokémon


🏗️ Arquitetura
O projeto segue a arquitetura MVVM com Clean Architecture, separado em 3 camadas principais:

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
Fluxo de Dados
1.  PokeAPI (Internet)
       ↓
2.  Retrofit (HTTP Client)
       ↓
3.  PokemonRepository (Cache Strategy)
       ↓
4.  Room Database (Persistência Local)
       ↓
5.  ViewModel (Estado da UI)
       ↓
6.  Compose Screen (Interface)
       ↓
7.  Usuário

Estratégia de Cache

Offline First: Dados são salvos localmente e exibidos imediatamente
Cache Inteligente: Reduz chamadas de rede desnecessárias
Sincronização: Atualiza cache em segundo plano quando online

Pré-requisitos

Android Studio Hedgehog (2023.1.1) ou superior
JDK 17 ou superior
Emulador Android ou Dispositivo físico com Android 7.0+ (API 24+)
Conexão com internet (para primeira execução)

// No Android Studio, vá em:
Device Manager → Seu dispositivo → Wipe Data
// Ou desinstale e reinstale o app
```

---

## **Estrutura do Projeto**
```
app/src/main/java/com/example/pokedex/
│
├── 📦 data/                           # Camada de Dados
│   ├── local/                         # Persistência Local (Room)
│   │   ├── dao/
│   │   │   ├── PokemonDao.kt          # Queries do cache de Pokémon
│   │   │   └── TeamDao.kt             # Queries do time do usuário
│   │   ├── entities/
│   │   │   ├── PokemonEntity.kt       # Tabela pokemon_cache
│   │   │   └── TeamMemberEntity.kt    # Tabela user_team
│   │   └── PokedexDatabase.kt         # Configuração do Room
│   │
│   ├── remote/                        # API Externa (Retrofit)
│   │   ├── dto/
│   │   │   ├── PokemonDetailDto.kt    # Modelo de resposta da API
│   │   │   └── PokemonListResponse.kt # Lista de Pokémon da API
│   │   └── PokeApiService.kt          # Interface Retrofit
│   │
│   └── repository/
│       ├── PokemonRepository.kt       # Coordena API + Cache
│       └── TeamRepository.kt          # Gerencia o time do usuário
│
├── 🎯 domain/                         # Camada de Domínio
│   └── model/
│       ├── Pokemon.kt                 # Modelo de Pokémon para UI
│       └── TeamMember.kt              # Modelo de membro do time
│
├── 🎨 ui/                             # Camada de Interface
│   ├── screens/
│   │   ├── pokemon_list/
│   │   │   ├── PokemonListScreen.kt   # Tela da Pokédex
│   │   │   └── PokemonListViewModel.kt
│   │   ├── pokemon_detail/
│   │   │   ├── PokemonDetailScreen.kt # Tela de detalhes
│   │   │   └── PokemonDetailViewModel.kt
│   │   └── team/
│   │       ├── TeamScreen.kt          # Tela do time
│   │       └── TeamViewModel.kt
│   │
│   └── theme/
│       ├── Color.kt                   # Paleta de cores
│       ├── Theme.kt                   # Tema Material 3
│       └── Type.kt                    # Tipografia
│
├── 💉 di/                             # Injeção de Dependências
│   └── AppModule.kt                   # Configuração Hilt
│
├── 📱 MainActivity.kt                 # Activity principal
└── 🚀 PokedexApplication.kt           # Application class

Integrantes:

Pedro Miguel Radwanski - 8132778953
John Claude Cameron Chappe - 813308540
Felipe de Mello Vieira - 3260115
Gustavo Rossi Silva - 4087158
























