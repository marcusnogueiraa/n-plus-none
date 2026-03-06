# n-plus-none 🚀

**Detector de queries N+1 em tempo de compilação para aplicações Java/Spring.**

O problema de N+1 queries é um dos maiores "vilões" de performance em aplicações que usam ORM (como Hibernate/JPA). Enquanto a maioria das ferramentas detecta isso apenas quando o código já está rodando, o **n-plus-none** encontra o erro durante a compilação.

### 💡 O Problema

No Spring Boot, é comum termos relacionamentos *Lazy-loaded*. Quando um desenvolvedor chama um getter (ex: `entidade.getRelacionamento()`) dentro de um loop, o Hibernate dispara uma nova consulta ao banco para cada iteração. Isso pode transformar uma única requisição em centenas de chamadas ao banco, derrubando o ambiente de produção.

### 🛠️ A Solução

O **n-plus-none** é um plugin Maven que analisa estaticamente a Árvore Sintática Abstrata (AST) do seu código fonte. Ele identifica:

* Loops `for-each` e `while` clássicos.
* Chamadas de métodos (getters) que disparam Lazy-load dentro desses loops.
* Interrompe o *build* automaticamente se violações forem encontradas, garantindo que apenas código otimizado chegue ao seu pipeline de CI/CD.

### 🚀 Como Usar

Adicione o plugin ao seu `pom.xml` (conforme exemplo acima) e execute:
`mvn nplusnone:check`

---

### Passo Final: O Commit

No terminal do projeto `n-plus-none`, execute:

```bash
git add README.md README.pt-br.md
git commit -m "docs: add project description and usage instructions in english and portuguese"

```