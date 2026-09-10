document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;

    try {
        const response = await fetch('http://localhost:8080/v2/Usuario/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, senha })
        });

        if (response.ok) {
            const data = await response.json();
            // Salva o token de acesso caso utilize Spring Security / JWT
            if (data.token) {
                localStorage.setItem('token', data.token);
            }
            window.location.href = '/dashboard.html';
        } else {
            alert('Credenciais inválidas. Verifique seu email e senha.');
        }
    } catch (error) {
        console.error('Erro na autenticação:', error);
        alert('Falha ao conectar com o servidor.');
    }
});