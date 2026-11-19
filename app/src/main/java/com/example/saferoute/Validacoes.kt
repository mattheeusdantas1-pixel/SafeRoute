package com.example.saferoute

const val SENHA_MINIMA = 6

// As validações devolvem null quando está tudo certo.
// Quando tem erro, devolvem o id da mensagem no strings.xml (R.string...),
// e a tela mostra o texto com stringResource(id).

// RN02: telefone com DDD, 10 (fixo) ou 11 (celular) dígitos
fun validarTelefone(telefone: String): Int? =
    if (telefone.length in 10..11) null else R.string.erro_telefone

// RF01: e-mail com formato válido. O Firebase também confere, mas assim a mensagem sai na hora e em português
fun validarEmail(email: String): Int? =
    if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) null else R.string.erro_email

// RF01 / RN04: nome, e-mail e telefone válidos, senha com 6+ caracteres e confirmada
fun validarCadastro(nome: String, email: String, telefone: String, senha: String, confirmacao: String): Int? =
    when {
        nome.isBlank() -> R.string.erro_nome_vazio
        validarEmail(email) != null -> R.string.erro_email
        validarTelefone(telefone) != null -> R.string.erro_telefone
        senha.length < SENHA_MINIMA -> R.string.erro_senha_curta
        senha != confirmacao -> R.string.erro_senhas_diferentes
        else -> null
    }
