// ==========================================================================
// Controle de Sessão e Proteção de Páginas
// ==========================================================================
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
            return;
        }
    }

    // Só carrega a lista se a página tiver a tabela de usuários
    if (document.getElementById('tabela-usuarios-body')) {
        carregarUsuarios();
        ativarBuscaUsuarios();
    }
});

// Função Utilitária de Logout (disponível globalmente)
function fazerLogout() {
    localStorage.removeItem('userToken');
    localStorage.removeItem('userName');
    localStorage.removeItem('userRole');
    window.location.href = 'index.html';
}

// ==========================================================================
// Tela de Usuários
// ==========================================================================

// Evita que texto vindo do banco seja interpretado como HTML
function escaparHtml(valor) {
    return String(valor ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

function mensagemNaTabela(tbody, texto, cor) {
    tbody.innerHTML = `
        <tr>
            <td colspan="6" style="text-align: center; color: ${cor}; padding: 24px;">
                ${texto}
            </td>
        </tr>`;
}

async function carregarUsuarios() {
    const tbody = document.getElementById('tabela-usuarios-body');
    if (!tbody) return;

    try {
        // Token guardado no login (chave 'userToken')
        const token = localStorage.getItem('userToken');

        // /v1/funcionarios devolve nome, email, cargo, nivelDeAcesso e ativo
        const response = await fetch('/v1/funcionarios', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.status === 401 || response.status === 403) {
            mensagemNaTabela(tbody, 'Você não tem permissão para ver esta lista (é preciso nível ADMIN).', '#ef4444');
            return;
        }

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }

        const funcionarios = await response.json();

        if (funcionarios.length === 0) {
            mensagemNaTabela(tbody, 'Nenhum usuário encontrado.', '#64748b');
            return;
        }

        tbody.innerHTML = '';

        funcionarios.forEach(user => {
            const iniciais = user.nome
                ? user.nome.split(' ').filter(Boolean).map(n => n[0]).join('').substring(0, 2).toUpperCase()
                : 'US';

            const statusTexto = user.ativo ? 'Ativo' : 'Inativo';
            const statusClass = user.ativo ? 'badge-active' : 'badge-inactive';

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>
                    <div class="user-cell">
                        <div class="user-avatar-table">${escaparHtml(iniciais)}</div>
                        <div class="user-info-text">
                            <span class="name">${escaparHtml(user.nome)}</span>
                            <span class="email">${escaparHtml(user.email)}</span>
                        </div>
                    </div>
                </td>
                <td>${escaparHtml(user.cargo || 'Não informado')}</td>
                <td><span class="badge badge-role">${escaparHtml(user.nivelDeAcesso || '-')}</span></td>
                <td><span class="badge ${statusClass}">${statusTexto}</span></td>
                <td>-</td>
                <td>
                    <div class="action-buttons" style="justify-content: flex-end;">
                        <button class="btn-icon" title="Editar Usuário" onclick="editarUsuario(${Number(user.id)})">
                            <i data-lucide="pencil"></i>
                        </button>
                        <button class="btn-icon danger" title="Inativar Usuário" onclick="excluirUsuario(${Number(user.id)})">
                            <i data-lucide="trash-2"></i>
                        </button>
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        });

        // Recria os ícones SVG do Lucide
        if (window.lucide) {
            lucide.createIcons();
        }

        // Reaplica o filtro se já houver texto na busca
        const busca = document.querySelector('.search-container input');
        if (busca && busca.value) busca.dispatchEvent(new Event('input'));

    } catch (error) {
        console.error('Erro ao carregar lista de usuários:', error);
        mensagemNaTabela(tbody, 'Erro ao carregar usuários do banco de dados.', '#ef4444');
    }
}

// Filtra as linhas da tabela conforme o texto digitado na busca
function ativarBuscaUsuarios() {
    const busca = document.querySelector('.search-container input');
    const tbody = document.getElementById('tabela-usuarios-body');
    if (!busca || !tbody) return;

    busca.addEventListener('input', () => {
        const termo = busca.value.trim().toLowerCase();
        tbody.querySelectorAll('tr').forEach(linha => {
            linha.hidden = !linha.textContent.toLowerCase().includes(termo);
        });
    });
}

function editarUsuario(id) {
    alert('A edição de usuários ainda não foi implementada (id ' + id + ').');
}

// O DELETE da API apenas inativa o funcionário (ativo = false)
async function excluirUsuario(id) {
    if (!confirm('Deseja realmente inativar este usuário?')) return;

    try {
        const token = localStorage.getItem('userToken');
        const response = await fetch(`/v1/funcionarios/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }

        carregarUsuarios();
    } catch (error) {
        console.error('Erro ao inativar usuário:', error);
        alert('Não foi possível inativar o usuário.');
    }
}
