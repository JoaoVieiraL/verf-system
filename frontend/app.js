// Controle de Sessão e Proteção de Páginas
document.addEventListener('DOMContentLoaded', () => {
    // Páginas acessíveis sem estar logado
    const paginasPublicas = ['index.html', 'esqueci-senha.html'];
    
    // Obtém o nome da página atual
    const paginaAtual = window.location.pathname.split('/').pop();

    // Valida a sessão em páginas restritas
    if (!paginasPublicas.includes(paginaAtual) && paginaAtual !== '') {
        const token = localStorage.getItem('userToken');

        if (!token) {
            alert('Sessão inválida ou expirada. Por favor, faça login.');
            window.location.href = 'index.html';
        }
    }
});

// Função Utilitária de Logout (disponível globalmente)
function fazerLogout() {
    localStorage.removeItem('userToken');
    localStorage.removeItem('userName');
    localStorage.removeItem('userRole');
    window.location.href = 'index.html';
}