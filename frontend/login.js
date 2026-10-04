const API_FUNCIONARIOS_URL = 'http://localhost:8090/auth/login';

document.addEventListener('DOMContentLoaded', () => {
    const formLogin = document.getElementById('login-form');

    if (formLogin) {
        formLogin.addEventListener('submit', async (event) => {
            // Evita recarregar a página e colocar os dados na URL
            event.preventDefault(); 

            const emailInput = document.getElementById('email');
            const senhaInput = document.getElementById('senha');

            if (!emailInput || !senhaInput) return;

            const emailDigitado = emailInput.value.trim();

            try {
                const resposta = await fetch(API_FUNCIONARIOS_URL, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        email: emailDigitado,
                        senha: senhaInput.value
                    })
                });

                 if (!resposta.ok) {
                    alert('E-mail ou senha inválidos.');
                    return;
                }
 
                const { token } = await resposta.json();
                localStorage.setItem('userToken', token);
                localStorage.setItem('userName', emailDigitado);
 
                window.location.href = 'modulos.html';
 

                const funcionarios = await resposta.json();

                // Busca o funcionário pelo e-mail cadastrado e ativo
                const usuarioEncontrado = funcionarios.find(
                    f => f.email && f.email.toLowerCase() === emailDigitado.toLowerCase() && f.ativo
                );

                if (usuarioEncontrado) {
                    const userId = usuarioEncontrado.id || usuarioEncontrado.idFuncionario || 1;
                    localStorage.setItem('userToken', 'sessao-ativa-' + userId);
                    localStorage.setItem('userName', usuarioEncontrado.nome);

                    alert(`Bem-vindo, ${usuarioEncontrado.nome}!`);
                    window.location.href = 'modulos.html';
                } else {
                    alert('E-mail não encontrado ou usuário inativo no sistema.');
                }

            } catch (erro) {
                console.error('Erro ao autenticar:', erro);
                alert('Não foi possível conectar ao back-end (http://localhost:8090). Verifique se a API está rodando.');
            }
        });
    }
});