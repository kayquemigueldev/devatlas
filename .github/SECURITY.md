# Política de Segurança

A segurança do DevAtlas é tratada com responsabilidade.
Relatos que ajudem a identificar vulnerabilidades são bem-vindos.

## Versões suportadas

O DevAtlas está em desenvolvimento ativo. Por isso, somente a versão
mais recente disponível na branch `main` recebe correções de segurança.

| Versão | Suporte |
|---|---|
| Branch `main` | ✅ |
| Versões anteriores | ❌ |

## Como reportar uma vulnerabilidade

Não abra uma Issue pública para relatar vulnerabilidades.

Utilize o canal privado do GitHub:

[Reportar uma vulnerabilidade](https://github.com/kayquemigueldev/devatlas/security/advisories/new)

Ao enviar o relato, inclua quando possível:

- descrição da vulnerabilidade;
- passos necessários para reproduzi-la;
- impacto esperado;
- versão, ambiente ou navegador utilizado;
- evidências sem dados pessoais ou credenciais reais;
- sugestão de correção, caso possua.

Nunca inclua tokens, senhas, chaves de API ou outros segredos reais
no relatório.

## Processo de análise

Após o recebimento do relato:

1. a vulnerabilidade será analisada;
2. o impacto e a possibilidade de reprodução serão avaliados;
3. uma correção será preparada e testada;
4. a divulgação pública ocorrerá somente após a correção, quando aplicável.

## Escopo

São considerados parte do escopo:

- código-fonte da aplicação;
- integração com a API do GitHub;
- processamento e armazenamento dos dados;
- imagem e configuração Docker;
- dependências Maven;
- workflows do GitHub Actions.

Problemas encontrados em serviços externos devem ser reportados
diretamente aos responsáveis por esses serviços.

## Divulgação responsável

Relatos feitos de boa-fé, sem exploração de dados reais e respeitando
a privacidade dos usuários, serão tratados como colaboração responsável.