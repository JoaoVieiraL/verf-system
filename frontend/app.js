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
            return;
        }
    }

    // Só carrega a lista se a página tiver a tabela de usuários
    if (document.getElementById('tabela-usuarios-body')) {
        carregarUsuarios();
        ativarBusca('tabela-usuarios-body');
        ativarFormularioUsuario();
    }

    // Só carrega a lista se a página tiver a tabela de fornecedores
    if (document.getElementById('tabela-fornecedores-body')) {
        carregarFornecedores();
        ativarBusca('tabela-fornecedores-body');
        ativarFormularioFornecedor();
    }

    // Só carrega a lista se a página tiver a tabela de estoque
    if (document.getElementById('tabela-estoque-body')) {
        carregarEstoque();
        ativarBusca('tabela-estoque-body');
        ativarFormularioMovimentacao();
        ativarFormularioTinta();
    }

    // Só carrega os indicadores se a página for o dashboard
    if (document.getElementById('card-total-tintas')) {
        carregarDashboard();
    }

    // Tela de Movimentações (histórico completo + nova movimentação)
    if (document.getElementById('tabela-lista-movimentacoes-body')) {
        carregarPaginaMovimentacoes();
        ativarFiltrosMovimentacoes();
        ativarFormularioMovimentacao();
    }

    // Tela de Mistura (receitas, dosagem e produção)
    if (document.getElementById('tabela-receitas-body')) {
        carregarReceitas();
        ativarBusca('tabela-receitas-body');
        ativarFormularioReceita();
        ativarFormularioProducao();
    }

    // Tela de Histórico (atividades e acessos)
    if (document.getElementById('tabela-atividades-body')) {
        carregarHistorico();
        ativarFiltrosHistorico();
    }

    // Tela de Relatórios (consumo e tintas mais usadas)
    if (document.getElementById('tabela-relatorio-tintas-body')) {
        iniciarRelatorios();
    }

    // Tela de Financeiro (fechamentos)
    if (document.getElementById('tabela-financeiro-body')) {
        carregarFinanceiro();
        ativarBusca('tabela-financeiro-body');
        ativarFormularioFinanceiro();
    }
});

// Função Utilitária de Logout (disponível globalmente)
function fazerLogout() {
    localStorage.removeItem('userToken');
    localStorage.removeItem('userName');
    localStorage.removeItem('userRole');
    window.location.href = 'index.html';
}

// Tela de Usuários
// Evita que texto vindo do banco seja interpretado como HTML
function escaparHtml(valor) {
    return String(valor ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

// Mensagem ocupando a largura da tabela (colunas = quantidade de colunas do thead)
function mensagemNaTabela(tbody, texto, cor, colunas = 6) {
    tbody.innerHTML = `
        <tr>
            <td colspan="${colunas}" style="text-align: center; color: ${cor}; padding: 24px;">
                ${texto}
            </td>
        </tr>`;
}

// Última lista recebida da API, usada para preencher o modal de edição
let usuariosCarregados = [];

async function carregarUsuarios() {
    const tbody = document.getElementById('tabela-usuarios-body');
    if (!tbody) return;

    try {
        // Token guardado no login (chave 'userToken')
        const token = localStorage.getItem('userToken');

        //funcionarios devolve nome, email, cargo, nivelDeAcesso e ativo
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
        usuariosCarregados = funcionarios;

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
                        <button class="btn-icon danger" title="Excluir Usuário" onclick="excluirUsuario(${Number(user.id)})">
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
function ativarBusca(tbodyId) {
    const busca = document.querySelector('.search-container input');
    const tbody = document.getElementById(tbodyId);
    if (!busca || !tbody) return;

    busca.addEventListener('input', () => {
        const termo = busca.value.trim().toLowerCase();
        tbody.querySelectorAll('tr').forEach(linha => {
            linha.hidden = !linha.textContent.toLowerCase().includes(termo);
        });
    });
}

// Modal de Usuário: sem parâmetro cadastra um novo; com usuário, edita
function abrirModalUsuario(usuario) {
    const modal = document.getElementById('modal-usuario');
    if (!modal) return;

    const form = document.getElementById('form-usuario');
    form.reset();
    mostrarErroUsuario('');

    const editando = Boolean(usuario);
    form.dataset.id = editando ? usuario.id : '';
    document.getElementById('modal-usuario-titulo').textContent = editando ? 'Editar Usuário' : 'Novo Usuário';
    document.getElementById('grupo-usuario-ativo').hidden = !editando;

    // Na edição a senha é opcional (em branco mantém a atual)
    document.getElementById('usuario-senha-obrigatoria').hidden = editando;
    document.getElementById('usuario-senha-dica').hidden = !editando;

    if (editando) {
        document.getElementById('usuario-nome').value = usuario.nome || '';
        document.getElementById('usuario-email').value = usuario.email || '';
        document.getElementById('usuario-cargo').value = usuario.cargo || '';
        document.getElementById('usuario-nivel').value = usuario.nivelDeAcesso || '';
        document.getElementById('usuario-ativo').checked = Boolean(usuario.ativo);
    }

    modal.hidden = false;
    document.getElementById('usuario-nome').focus();
}

function fecharModalUsuario() {
    const modal = document.getElementById('modal-usuario');
    if (modal) modal.hidden = true;
}

function mostrarErroUsuario(texto) {
    const caixa = document.getElementById('modal-usuario-erro');
    if (!caixa) return;
    caixa.textContent = texto;
    caixa.hidden = !texto;
}

function ativarFormularioUsuario() {
    const form = document.getElementById('form-usuario');
    const modal = document.getElementById('modal-usuario');
    if (!form || !modal) return;

    // Fecha ao clicar fora do card ou apertar Esc
    modal.addEventListener('click', (e) => {
        if (e.target === modal) fecharModalUsuario();
    });
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && !modal.hidden) fecharModalUsuario();
    });

    form.addEventListener('submit', salvarUsuario);
}

async function salvarUsuario(event) {
    event.preventDefault();

    // Com id no formulário é edição (PUT); sem id é cadastro (POST)
    const id = event.target.dataset.id;

    const dados = {
        nome: document.getElementById('usuario-nome').value.trim(),
        email: document.getElementById('usuario-email').value.trim(),
        cargo: document.getElementById('usuario-cargo').value,
        nivelDeAcesso: document.getElementById('usuario-nivel').value,
        senha: document.getElementById('usuario-senha').value
    };

    if (!dados.nome || !dados.email || !dados.cargo || !dados.nivelDeAcesso || (!id && !dados.senha)) {
        mostrarErroUsuario('Preencha todos os campos obrigatórios.');
        return;
    }
    if (!document.getElementById('usuario-email').checkValidity()) {
        mostrarErroUsuario('Informe um e-mail válido.');
        return;
    }
    if (dados.senha && dados.senha.length < 8) {
        mostrarErroUsuario('A senha precisa ter no mínimo 8 caracteres.');
        return;
    }

    if (id) {
        dados.ativo = document.getElementById('usuario-ativo').checked;
        // Senha em branco não é enviada, assim a API mantém a atual
        if (!dados.senha) delete dados.senha;
    }

    const botao = document.getElementById('btn-salvar-usuario');
    botao.disabled = true;
    mostrarErroUsuario('');

    try {
        const token = localStorage.getItem('userToken');
        const response = await fetch(id ? `/v1/funcionarios/${id}` : '/v1/funcionarios', {
            method: id ? 'PUT' : 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(dados)
        });

        if (response.status === 401 || response.status === 403) {
            mostrarErroUsuario('Você não tem permissão para salvar usuários (é preciso nível ADMIN).');
            return;
        }

        // 409 = e-mail já cadastrado; 400 = regra de negócio. A API explica no campo "mensagem"
        if (response.status === 409 || response.status === 400) {
            const erro = await response.json().catch(() => ({}));
            mostrarErroUsuario(erro.mensagem || 'Dados inválidos. Confira os campos e tente novamente.');
            return;
        }

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }

        fecharModalUsuario();
        carregarUsuarios();
    } catch (error) {
        console.error('Erro ao salvar usuário:', error);
        mostrarErroUsuario('Não foi possível salvar o usuário.');
    } finally {
        botao.disabled = false;
    }
}

function editarUsuario(id) {
    const usuario = usuariosCarregados.find(u => Number(u.id) === id);
    if (!usuario) {
        alert('Usuário não encontrado. Recarregue a página e tente novamente.');
        return;
    }
    abrirModalUsuario(usuario);
}

// O DELETE da API remove o usuário do banco (bloqueado se houver registros vinculados)
async function excluirUsuario(id) {
    const usuario = usuariosCarregados.find(u => Number(u.id) === id);
    const nome = usuario ? `"${usuario.nome}"` : 'este usuário';
    if (!confirm(`Deseja realmente excluir ${nome}? Essa ação não pode ser desfeita.`)) return;

    try {
        const token = localStorage.getItem('userToken');
        const response = await fetch(`/v1/funcionarios/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.status === 401 || response.status === 403) {
            alert('Você não tem permissão para excluir usuários (é preciso nível ADMIN).');
            return;
        }

        // 400 = próprio usuário ou registros vinculados; a API explica no campo "mensagem"
        if (response.status === 400 || response.status === 404) {
            const erro = await response.json().catch(() => ({}));
            alert(erro.mensagem || 'Não foi possível excluir o usuário.');
            return;
        }

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }

        carregarUsuarios();
    } catch (error) {
        console.error('Erro ao excluir usuário:', error);
        alert('Não foi possível excluir o usuário.');
    }
}

// Tela de Fornecedores
// Formata 14 dígitos como 00.000.000/0000-00; outros valores ficam como vieram
// (o CNPJ não é validado, então números fictícios são aceitos)
function formatarCnpj(cnpj) {
    const digitos = String(cnpj ?? '').replace(/\D/g, '');
    if (digitos.length !== 14) return cnpj || '-';
    return digitos.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, '$1.$2.$3/$4-$5');
}

function formatarData(valor) {
    if (!valor) return '-';
    const data = new Date(valor);
    return isNaN(data) ? '-' : data.toLocaleDateString('pt-BR');
}

// Última lista recebida da API, usada para preencher o modal de edição
let fornecedoresCarregados = [];

async function carregarFornecedores() {
    const tbody = document.getElementById('tabela-fornecedores-body');
    if (!tbody) return;

    try {
        const token = localStorage.getItem('userToken');

        // /v1/fornecedores devolve idFornecedor, cnpj, nome, telefone, email, ativo e criadoEm
        const response = await fetch('/v1/fornecedores', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.status === 401 || response.status === 403) {
            mensagemNaTabela(tbody, 'Você não tem permissão para ver esta lista.', '#ef4444');
            return;
        }

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }

        const fornecedores = await response.json();
        fornecedoresCarregados = fornecedores;

        if (fornecedores.length === 0) {
            mensagemNaTabela(tbody, 'Nenhum fornecedor encontrado.', '#64748b');
            return;
        }

        tbody.innerHTML = '';

        fornecedores.forEach(fornecedor => {
            const iniciais = fornecedor.nome
                ? fornecedor.nome.split(' ').filter(Boolean).map(n => n[0]).join('').substring(0, 2).toUpperCase()
                : 'FO';

            const statusTexto = fornecedor.ativo ? 'Ativo' : 'Inativo';
            const statusClass = fornecedor.ativo ? 'badge-active' : 'badge-inactive';
            const id = Number(fornecedor.idFornecedor);

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>
                    <div class="user-cell">
                        <div class="user-avatar-table">${escaparHtml(iniciais)}</div>
                        <div class="user-info-text">
                            <span class="name">${escaparHtml(fornecedor.nome)}</span>
                            <span class="email">${escaparHtml(fornecedor.email || 'Sem e-mail')}</span>
                        </div>
                    </div>
                </td>
                <td class="nowrap">${escaparHtml(formatarCnpj(fornecedor.cnpj))}</td>
                <td class="nowrap">${escaparHtml(fornecedor.telefone || 'Não informado')}</td>
                <td><span class="badge ${statusClass}">${statusTexto}</span></td>
                <td>${escaparHtml(formatarData(fornecedor.criadoEm))}</td>
                <td>
                    <div class="action-buttons" style="justify-content: flex-end;">
                        <button class="btn-icon" title="Editar Fornecedor" onclick="editarFornecedor(${id})">
                            <i data-lucide="pencil"></i>
                        </button>
                        <button class="btn-icon danger" title="Excluir Fornecedor" onclick="excluirFornecedor(${id})">
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
        console.error('Erro ao carregar lista de fornecedores:', error);
        mensagemNaTabela(tbody, 'Erro ao carregar fornecedores do banco de dados.', '#ef4444');
    }
}

// Modal de Fornecedor: sem parâmetro cadastra um novo; com fornecedor, edita
function abrirModalFornecedor(fornecedor) {
    const modal = document.getElementById('modal-fornecedor');
    if (!modal) return;

    const form = document.getElementById('form-fornecedor');
    form.reset();
    mostrarErroFornecedor('');

    const editando = Boolean(fornecedor);
    form.dataset.id = editando ? fornecedor.idFornecedor : '';
    document.getElementById('modal-fornecedor-titulo').textContent = editando ? 'Editar Fornecedor' : 'Novo Fornecedor';
    document.getElementById('grupo-fornecedor-ativo').hidden = !editando;

    if (editando) {
        document.getElementById('fornecedor-nome').value = fornecedor.nome || '';
        document.getElementById('fornecedor-telefone').value = fornecedor.telefone || '';
        document.getElementById('fornecedor-email').value = fornecedor.email || '';
        document.getElementById('fornecedor-ativo').checked = Boolean(fornecedor.ativo);

        // Dispara o evento para aplicar a máscara no CNPJ preenchido
        const campoCnpj = document.getElementById('fornecedor-cnpj');
        campoCnpj.value = fornecedor.cnpj || '';
        campoCnpj.dispatchEvent(new Event('input'));
    }

    modal.hidden = false;
    document.getElementById('fornecedor-nome').focus();
}

function fecharModalFornecedor() {
    const modal = document.getElementById('modal-fornecedor');
    if (modal) modal.hidden = true;
}

function mostrarErroFornecedor(texto) {
    const caixa = document.getElementById('modal-fornecedor-erro');
    if (!caixa) return;
    caixa.textContent = texto;
    caixa.hidden = !texto;
}

function ativarFormularioFornecedor() {
    const form = document.getElementById('form-fornecedor');
    const modal = document.getElementById('modal-fornecedor');
    if (!form || !modal) return;

    // Aplica a máscara do CNPJ enquanto o usuário digita
    const campoCnpj = document.getElementById('fornecedor-cnpj');
    campoCnpj.addEventListener('input', () => {
        const d = campoCnpj.value.replace(/\D/g, '').substring(0, 14);
        campoCnpj.value = d
            .replace(/^(\d{2})(\d)/, '$1.$2')
            .replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
            .replace(/\.(\d{3})(\d)/, '.$1/$2')
            .replace(/(\d{4})(\d)/, '$1-$2');
    });

    // Fecha ao clicar fora do card ou apertar Esc
    modal.addEventListener('click', (e) => {
        if (e.target === modal) fecharModalFornecedor();
    });
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && !modal.hidden) fecharModalFornecedor();
    });

    form.addEventListener('submit', salvarFornecedor);
}

async function salvarFornecedor(event) {
    event.preventDefault();

    const dados = {
        nome: document.getElementById('fornecedor-nome').value.trim(),
        cnpj: document.getElementById('fornecedor-cnpj').value.replace(/\D/g, ''),
        telefone: document.getElementById('fornecedor-telefone').value.trim(),
        email: document.getElementById('fornecedor-email').value.trim()
    };

    if (!dados.nome || !dados.cnpj || !dados.telefone || !dados.email) {
        mostrarErroFornecedor('Preencha todos os campos obrigatórios.');
        return;
    }
    if (dados.cnpj.length !== 14) {
        mostrarErroFornecedor('O CNPJ precisa ter 14 dígitos.');
        return;
    }
    if (!document.getElementById('fornecedor-email').checkValidity()) {
        mostrarErroFornecedor('Informe um e-mail válido.');
        return;
    }

    // Com id no formulário é edição (PUT); sem id é cadastro (POST)
    const id = event.target.dataset.id;
    if (id) {
        dados.ativo = document.getElementById('fornecedor-ativo').checked;
    }

    const botao = document.getElementById('btn-salvar-fornecedor');
    botao.disabled = true;
    mostrarErroFornecedor('');

    try {
        const token = localStorage.getItem('userToken');
        const response = await fetch(id ? `/v1/fornecedores/${id}` : '/v1/fornecedores', {
            method: id ? 'PUT' : 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(dados)
        });

        if (response.status === 401 || response.status === 403) {
            mostrarErroFornecedor('Você não tem permissão para salvar fornecedores (é preciso nível GERENTE ou ADMIN).');
            return;
        }

        // 409 = CNPJ já cadastrado; a API devolve a mensagem no campo "mensagem"
        if (response.status === 409) {
            const erro = await response.json().catch(() => ({}));
            mostrarErroFornecedor(erro.mensagem || 'Já existe um fornecedor com este CNPJ.');
            return;
        }

        if (response.status === 400) {
            mostrarErroFornecedor('Dados inválidos. Confira se o e-mail está correto.');
            return;
        }

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }

        fecharModalFornecedor();
        carregarFornecedores();
    } catch (error) {
        console.error('Erro ao salvar fornecedor:', error);
        mostrarErroFornecedor('Não foi possível salvar o fornecedor.');
    } finally {
        botao.disabled = false;
    }
}

function editarFornecedor(id) {
    const fornecedor = fornecedoresCarregados.find(f => Number(f.idFornecedor) === id);
    if (!fornecedor) {
        alert('Fornecedor não encontrado. Recarregue a página e tente novamente.');
        return;
    }
    abrirModalFornecedor(fornecedor);
}

// O DELETE da API remove o fornecedor do banco (bloqueado se houver tintas vinculadas)
async function excluirFornecedor(id) {
    const fornecedor = fornecedoresCarregados.find(f => Number(f.idFornecedor) === id);
    const nome = fornecedor ? `"${fornecedor.nome}"` : 'este fornecedor';
    if (!confirm(`Deseja realmente excluir ${nome}? Essa ação não pode ser desfeita.`)) return;

    try {
        const token = localStorage.getItem('userToken');
        const response = await fetch(`/v1/fornecedores/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.status === 401 || response.status === 403) {
            alert('Você não tem permissão para excluir fornecedores (é preciso nível GERENTE ou ADMIN).');
            return;
        }

        // 400 = fornecedor com tintas vinculadas; a API explica no campo "mensagem"
        if (response.status === 400 || response.status === 404) {
            const erro = await response.json().catch(() => ({}));
            alert(erro.mensagem || 'Não foi possível excluir o fornecedor.');
            return;
        }

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }

        carregarFornecedores();
    } catch (error) {
        console.error('Erro ao excluir fornecedor:', error);
        alert('Não foi possível excluir o fornecedor.');
    }
}

// Tela de Estoque
// Abaixo deste valor o item aparece como "Estoque baixo"
const ESTOQUE_MINIMO = 10;

// Última lista recebida da API, usada para preencher o modal de movimentação
let estoqueCarregado = [];

// Só aceita cores no formato #RRGGBB para usar com segurança no style
function corValida(hex) {
    return /^#[0-9A-Fa-f]{6}$/.test(hex || '') ? hex : null;
}

function situacaoEstoque(quantidade) {
    if (quantidade <= 0) return { texto: 'Sem estoque', classe: 'badge-inactive' };
    if (quantidade < ESTOQUE_MINIMO) return { texto: 'Estoque baixo', classe: 'badge-warning' };
    return { texto: 'Normal', classe: 'badge-active' };
}

async function carregarEstoque() {
    const tbody = document.getElementById('tabela-estoque-body');
    if (!tbody) return;

    try {
        const token = localStorage.getItem('userToken');

        // /v1/estoques devolve idEstoque, idTinta, nomeTinta, codigoTinta, corHexadecimal, origemTinta, quantidade e datas
        const response = await fetch('/v1/estoques', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.status === 401 || response.status === 403) {
            mensagemNaTabela(tbody, 'Você não tem permissão para ver esta lista.', '#ef4444');
            return;
        }

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }

        const itens = await response.json();
        estoqueCarregado = itens;

        if (itens.length === 0) {
            mensagemNaTabela(tbody, 'Nenhuma tinta no estoque.', '#64748b');
            return;
        }

        tbody.innerHTML = '';

        itens.forEach(item => {
            const cor = corValida(item.corHexadecimal);
            const iniciais = item.nomeTinta
                ? item.nomeTinta.split(' ').filter(Boolean).map(n => n[0]).join('').substring(0, 2).toUpperCase()
                : 'TI';
            const situacao = situacaoEstoque(Number(item.quantidade));
            const origem = item.origemTinta === 'PRODUZIDA' ? 'Produzida' : item.origemTinta === 'COMPRADA' ? 'Comprada' : '-';
            const id = Number(item.idEstoque);

            // Com cor cadastrada mostra a amostra; sem cor mostra as iniciais
            const avatar = cor
                ? `<div class="user-avatar-table cor-tinta" style="background-color: ${cor};" title="${cor}"></div>`
                : `<div class="user-avatar-table">${escaparHtml(iniciais)}</div>`;

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>
                    <div class="user-cell">
                        ${avatar}
                        <div class="user-info-text">
                            <span class="name">${escaparHtml(item.nomeTinta || 'Tinta sem nome')}</span>
                            <span class="email">${escaparHtml(item.codigoTinta || 'Sem código')}</span>
                        </div>
                    </div>
                </td>
                <td><span class="badge badge-role">${origem}</span></td>
                <td class="quantidade">${escaparHtml(item.quantidade)}</td>
                <td><span class="badge ${situacao.classe}">${situacao.texto}</span></td>
                <td>${escaparHtml(formatarData(item.atualizadoEm))}</td>
                <td>
                    <div class="action-buttons" style="justify-content: flex-end;">
                        <button class="btn-icon" title="Registrar Movimentação" onclick="abrirModalMovimentacao(${id})">
                            <i data-lucide="arrow-left-right"></i>
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
        console.error('Erro ao carregar estoque:', error);
        mensagemNaTabela(tbody, 'Erro ao carregar o estoque do banco de dados.', '#ef4444');
    }
}

// Modal de Movimentação: o saldo do estoque só muda registrando uma entrada ou saída
// FABRICADA e CONSUMIDA não aparecem porque são geradas automaticamente pela produção
const MOTIVOS_POR_TIPO = {
    ENTRADA: ['COMPRA', 'AJUSTE'],
    SAIDA: ['VENDA', 'PERDA', 'DANO', 'AJUSTE']
};

// Item do estoque que está aberto no modal
let itemMovimentacao = null;

function tipoMovimentacaoSelecionado() {
    const marcado = document.querySelector('input[name="mov-tipo"]:checked');
    return marcado ? marcado.value : 'ENTRADA';
}

// Recria as opções de motivo conforme o tipo (entrada ou saída)
function preencherMotivos() {
    const select = document.getElementById('mov-motivo');
    select.innerHTML = '';
    MOTIVOS_POR_TIPO[tipoMovimentacaoSelecionado()].forEach(motivo => {
        const option = document.createElement('option');
        option.value = motivo;
        option.textContent = NOMES_MOTIVOS[motivo] || motivo;
        select.appendChild(option);
    });
    atualizarObservacaoObrigatoria();
}

// Ajuste exige justificativa no campo observação
function atualizarObservacaoObrigatoria() {
    const ehAjuste = document.getElementById('mov-motivo').value === 'AJUSTE';
    document.getElementById('mov-observacao-obrigatoria').hidden = !ehAjuste;
    document.getElementById('mov-observacao-dica').hidden = !ehAjuste;
    document.getElementById('mov-observacao').placeholder = ehAjuste ? 'Explique o motivo do ajuste' : 'Opcional';
}

// Mostra como o saldo vai ficar depois de registrar
function atualizarPreviaSaldo() {
    if (!itemMovimentacao) return;
    const saldo = Number(itemMovimentacao.quantidade) || 0;
    const qtd = Number(document.getElementById('mov-quantidade').value) || 0;
    const depois = tipoMovimentacaoSelecionado() === 'ENTRADA' ? saldo + qtd : saldo - qtd;

    document.getElementById('mov-saldo-depois').textContent = depois;
    document.getElementById('mov-previa').classList.toggle('negativo', depois < 0);
}

function abrirModalMovimentacao(id) {
    const item = estoqueCarregado.find(e => Number(e.idEstoque) === id);
    const modal = document.getElementById('modal-movimentacao');
    if (!item || !modal) {
        alert('Item não encontrado. Recarregue a página e tente novamente.');
        return;
    }

    document.getElementById('form-movimentacao').reset();
    mostrarErroMovimentacao('');
    selecionarItemMovimentacao(item);
    preencherMotivos();
    atualizarPreviaSaldo();

    modal.hidden = false;
    document.getElementById('mov-quantidade').focus();
}

// Mostra no modal a tinta escolhida e o saldo atual dela
function selecionarItemMovimentacao(item) {
    itemMovimentacao = item;

    const cor = corValida(item.corHexadecimal);
    document.getElementById('mov-cor').style.backgroundColor = cor || '#154ec1';
    document.getElementById('mov-tinta-nome').textContent = item.nomeTinta || 'Tinta sem nome';
    document.getElementById('mov-tinta-codigo').textContent = item.codigoTinta || 'Sem código';
    document.getElementById('mov-saldo-atual').textContent = item.quantidade;
}

// Tela de Movimentações: abre o modal com um select para escolher a tinta
async function abrirModalNovaMovimentacao() {
    const modal = document.getElementById('modal-movimentacao');
    const select = document.getElementById('mov-estoque');
    if (!modal || !select) return;

    document.getElementById('form-movimentacao').reset();
    mostrarErroMovimentacao('');

    // Busca o saldo atualizado de cada tinta antes de abrir
    estoqueCarregado = await buscarLista('/v1/estoques');
    if (estoqueCarregado.length === 0) {
        alert('Nenhuma tinta no estoque. Cadastre uma tinta primeiro.');
        return;
    }

    select.innerHTML = '';
    [...estoqueCarregado]
        .sort((a, b) => String(a.nomeTinta).localeCompare(String(b.nomeTinta)))
        .forEach(item => {
            const option = document.createElement('option');
            option.value = item.idEstoque;
            option.textContent = item.codigoTinta ? `${item.nomeTinta} (${item.codigoTinta})` : item.nomeTinta;
            select.appendChild(option);
        });

    trocarTintaMovimentacao();
    preencherMotivos();
    modal.hidden = false;
    select.focus();
}

function trocarTintaMovimentacao() {
    const select = document.getElementById('mov-estoque');
    const item = estoqueCarregado.find(e => String(e.idEstoque) === select.value);
    if (item) selecionarItemMovimentacao(item);
    atualizarPreviaSaldo();
}

function fecharModalMovimentacao() {
    const modal = document.getElementById('modal-movimentacao');
    if (modal) modal.hidden = true;
    itemMovimentacao = null;
}

function mostrarErroMovimentacao(texto) {
    const caixa = document.getElementById('modal-movimentacao-erro');
    if (!caixa) return;
    caixa.textContent = texto;
    caixa.hidden = !texto;
}

function ativarFormularioMovimentacao() {
    const form = document.getElementById('form-movimentacao');
    const modal = document.getElementById('modal-movimentacao');
    if (!form || !modal) return;

    document.querySelectorAll('input[name="mov-tipo"]').forEach(radio => {
        radio.addEventListener('change', () => {
            preencherMotivos();
            atualizarPreviaSaldo();
        });
    });
    document.getElementById('mov-motivo').addEventListener('change', atualizarObservacaoObrigatoria);

    // Só existe na tela de Movimentações, onde a tinta é escolhida no próprio modal
    const selectEstoque = document.getElementById('mov-estoque');
    if (selectEstoque) selectEstoque.addEventListener('change', trocarTintaMovimentacao);
    document.getElementById('mov-quantidade').addEventListener('input', atualizarPreviaSaldo);

    // Fecha ao clicar fora do card ou apertar Esc
    modal.addEventListener('click', (e) => {
        if (e.target === modal) fecharModalMovimentacao();
    });
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && !modal.hidden) fecharModalMovimentacao();
    });

    form.addEventListener('submit', salvarMovimentacao);
}

async function salvarMovimentacao(event) {
    event.preventDefault();
    if (!itemMovimentacao) return;

    const tipo = tipoMovimentacaoSelecionado();
    const motivo = document.getElementById('mov-motivo').value;
    const textoQuantidade = document.getElementById('mov-quantidade').value.trim();
    const quantidade = Number(textoQuantidade);
    const observacao = document.getElementById('mov-observacao').value.trim();
    const saldo = Number(itemMovimentacao.quantidade) || 0;

    if (!textoQuantidade || !Number.isInteger(quantidade) || quantidade <= 0) {
        mostrarErroMovimentacao('A quantidade precisa ser um número inteiro maior que zero.');
        return;
    }
    if (tipo === 'SAIDA' && quantidade > saldo) {
        mostrarErroMovimentacao(`A saída não pode ser maior que o saldo atual (${saldo}).`);
        return;
    }
    if (motivo === 'AJUSTE' && !observacao) {
        mostrarErroMovimentacao('Ajuste exige uma justificativa no campo observação.');
        return;
    }

    const dados = {
        idEstoque: Number(itemMovimentacao.idEstoque),
        tipoMovimentacao: tipo,
        motivo,
        quantidadeMovimentada: quantidade,
        observacao: observacao || null
    };

    const botao = document.getElementById('btn-salvar-movimentacao');
    botao.disabled = true;
    mostrarErroMovimentacao('');

    try {
        const token = localStorage.getItem('userToken');
        const response = await fetch('/v1/movimentacao-estoque', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(dados)
        });

        // A API explica o motivo no campo "mensagem" (sem permissão, saldo insuficiente, motivo inválido...)
        if (!response.ok) {
            const erro = await response.json().catch(() => ({}));
            if (response.status === 401 || response.status === 403) {
                mostrarErroMovimentacao(erro.mensagem || 'Você não tem permissão para registrar movimentações (é preciso nível OPERADOR ou superior).');
            } else {
                mostrarErroMovimentacao(erro.mensagem || 'Não foi possível registrar a movimentação.');
            }
            return;
        }

        fecharModalMovimentacao();
        // Recarrega a tabela da tela aberta (cada função ignora se a tabela não existir)
        carregarEstoque();
        carregarPaginaMovimentacoes();
    } catch (error) {
        console.error('Erro ao registrar movimentação:', error);
        mostrarErroMovimentacao('Não foi possível registrar a movimentação.');
    } finally {
        botao.disabled = false;
    }
}

// Tela de Dashboard
const NOMES_MESES = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];

// Lista os últimos N meses (do mais antigo ao atual) com chave "ano-mês" e rótulo curto
function ultimosMeses(quantidade) {
    const hoje = new Date();
    const meses = [];
    for (let i = quantidade - 1; i >= 0; i--) {
        const data = new Date(hoje.getFullYear(), hoje.getMonth() - i, 1);
        meses.push({ chave: `${data.getFullYear()}-${data.getMonth()}`, rotulo: NOMES_MESES[data.getMonth()] });
    }
    return meses;
}

function chaveMes(valor) {
    const data = new Date(valor);
    return isNaN(data) ? null : `${data.getFullYear()}-${data.getMonth()}`;
}

// Faz um GET na API; se falhar (sem permissão, erro), devolve lista vazia para o dashboard não quebrar
async function buscarLista(url) {
    try {
        const token = localStorage.getItem('userToken');
        const response = await fetch(url, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) {
            console.warn(`Dashboard: ${url} respondeu ${response.status}`);
            return [];
        }
        return await response.json();
    } catch (error) {
        console.error(`Dashboard: erro ao buscar ${url}`, error);
        return [];
    }
}

// Devolve a chave com o maior total em um Map (ou null se estiver vazio)
function maiorDoMapa(mapa) {
    let melhor = null;
    mapa.forEach((total, chave) => {
        if (melhor === null || total > melhor.total) melhor = { chave, total };
    });
    return melhor;
}

function somarNoMapa(mapa, chave, valor) {
    if (chave === null || chave === undefined) return;
    mapa.set(chave, (mapa.get(chave) || 0) + valor);
}

function preencherIndicador(idValor, idSub, valor, sub) {
    document.getElementById(idValor).textContent = valor;
    document.getElementById(idSub).textContent = sub;
}

async function carregarDashboard() {
    const [tintas, estoques, movimentacoes, fornecedores, receitas, producoes] = await Promise.all([
        buscarLista('/v1/tintas'),
        buscarLista('/v1/estoques'),
        buscarLista('/v1/movimentacao-estoque'),
        buscarLista('/v1/fornecedores'),
        buscarLista('/v1/receitas'),
        buscarLista('/v1/producoes')
    ]);

    // Cards de resumo
    document.getElementById('card-total-tintas').textContent = tintas.filter(t => t.ativo).length;

    const alertas = estoques
        .filter(e => Number(e.quantidade) < ESTOQUE_MINIMO)
        .sort((a, b) => Number(a.quantidade) - Number(b.quantidade));
    document.getElementById('card-alertas').textContent = alertas.length;

    const mesAtual = chaveMes(new Date());
    let entradasMes = 0;
    let saidasMes = 0;

    // Gráfico: soma entradas e saídas dos últimos 6 meses
    const meses = ultimosMeses(6);
    const porMes = new Map(meses.map(m => [m.chave, { mes: m.rotulo, entradas: 0, saidas: 0 }]));

    // Indicadores: totais por estoque
    const saidasPorEstoque = new Map();
    const movimentacoesPorEstoque = new Map();

    movimentacoes.forEach(mov => {
        const qtd = Number(mov.quantidadeMovimentada) || 0;
        const chave = chaveMes(mov.dataMovimentacao);
        const ehEntrada = mov.tipoMovimentacao === 'ENTRADA';
        const ehSaida = mov.tipoMovimentacao === 'SAIDA';

        if (chave === mesAtual) {
            if (ehEntrada) entradasMes += qtd;
            if (ehSaida) saidasMes += qtd;
        }

        const mes = porMes.get(chave);
        if (mes) {
            if (ehEntrada) mes.entradas += qtd;
            if (ehSaida) mes.saidas += qtd;
        }

        if (ehSaida) somarNoMapa(saidasPorEstoque, mov.estoqueId, qtd);
        somarNoMapa(movimentacoesPorEstoque, mov.estoqueId, 1);
    });

    document.getElementById('card-entradas').textContent = entradasMes;
    document.getElementById('card-saidas').textContent = saidasMes;

    if (typeof atualizarGrafico === 'function') {
        atualizarGrafico(Array.from(porMes.values()));
    }

    renderizarAlertas(alertas);
    renderizarUltimasMovimentacoes(movimentacoes, estoques);

    // Nome da tinta a partir do id do estoque
    const nomePorEstoque = new Map(estoques.map(e => [e.idEstoque, e.nomeTinta || 'Tinta sem nome']));

    const tintaMaisUsada = maiorDoMapa(saidasPorEstoque);
    if (tintaMaisUsada) {
        preencherIndicador('ind-tinta-usada', 'sub-tinta-usada',
            nomePorEstoque.get(tintaMaisUsada.chave) || 'Tinta removida', `${tintaMaisUsada.total} un. consumidas`);
    }

    const maisMovimentado = maiorDoMapa(movimentacoesPorEstoque);
    if (maisMovimentado) {
        preencherIndicador('ind-movimentado', 'sub-movimentado',
            nomePorEstoque.get(maisMovimentado.chave) || 'Tinta removida',
            `${maisMovimentado.total} ${maisMovimentado.total === 1 ? 'movimentação' : 'movimentações'}`);
    }

    // Fornecedor com mais tintas cadastradas
    const tintasPorFornecedor = new Map();
    tintas.forEach(t => somarNoMapa(tintasPorFornecedor, t.fornecedorId, 1));
    const fornecedorPrincipal = maiorDoMapa(tintasPorFornecedor);
    if (fornecedorPrincipal) {
        const fornecedor = fornecedores.find(f => Number(f.idFornecedor) === Number(fornecedorPrincipal.chave));
        preencherIndicador('ind-fornecedor', 'sub-fornecedor',
            fornecedor ? fornecedor.nome : 'Fornecedor removido',
            `${fornecedorPrincipal.total} ${fornecedorPrincipal.total === 1 ? 'tinta' : 'tintas'}`);
    }

    // Receita com mais produções
    const producoesPorReceita = new Map();
    producoes.forEach(p => somarNoMapa(producoesPorReceita, p.receitaRefId, 1));
    const receitaMaisUsada = maiorDoMapa(producoesPorReceita);
    if (receitaMaisUsada) {
        const receita = receitas.find(r => Number(r.id) === Number(receitaMaisUsada.chave));
        preencherIndicador('ind-receita', 'sub-receita',
            receita ? receita.nome : 'Receita removida',
            `${receitaMaisUsada.total} ${receitaMaisUsada.total === 1 ? 'produção' : 'produções'}`);
    }
}

// Quantas linhas mostrar na tabela de últimas movimentações
const LIMITE_ULTIMAS_MOVIMENTACOES = 8;

const NOMES_MOTIVOS = {
    AJUSTE: 'Ajuste',
    PERDA: 'Perda',
    DANO: 'Dano',
    VENDA: 'Venda',
    COMPRA: 'Compra',
    FABRICADA: 'Fabricada',
    CONSUMIDA: 'Consumida'
};

function formatarDataHora(valor) {
    if (!valor) return '-';
    const data = new Date(valor);
    if (isNaN(data)) return '-';
    return data.toLocaleString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

// Mostra as movimentações mais recentes, com a tinta de cada estoque
function renderizarUltimasMovimentacoes(movimentacoes, estoques) {
    const tbody = document.getElementById('tabela-movimentacoes-body');
    if (!tbody) return;

    if (movimentacoes.length === 0) {
        mensagemNaTabela(tbody, 'Nenhuma movimentação registrada.', '#64748b');
        return;
    }

    const estoquePorId = new Map(estoques.map(e => [e.idEstoque, e]));
    const recentes = [...movimentacoes]
        .sort((a, b) => new Date(b.dataMovimentacao) - new Date(a.dataMovimentacao))
        .slice(0, LIMITE_ULTIMAS_MOVIMENTACOES);

    tbody.innerHTML = '';

    recentes.forEach(mov => {
        const estoque = estoquePorId.get(mov.estoqueId);
        const cor = estoque ? corValida(estoque.corHexadecimal) : null;
        const ehEntrada = mov.tipoMovimentacao === 'ENTRADA';
        const qtd = Number(mov.quantidadeMovimentada) || 0;

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>
                <div class="tinta-cell">
                    <div class="alerta-cor" ${cor ? `style="background-color: ${cor};"` : ''}></div>
                    <div class="alerta-info">
                        <span class="nome">${escaparHtml(estoque ? estoque.nomeTinta : 'Tinta removida')}</span>
                        <span class="codigo">${escaparHtml(estoque ? estoque.codigoTinta : '-')}</span>
                    </div>
                </div>
            </td>
            <td><span class="badge ${ehEntrada ? 'badge-active' : 'badge-inactive'}">${ehEntrada ? 'Entrada' : 'Saída'}</span></td>
            <td>${escaparHtml(NOMES_MOTIVOS[mov.motivo] || mov.motivo || '-')}</td>
            <td class="${ehEntrada ? 'qtd-entrada' : 'qtd-saida'}">${ehEntrada ? '+' : '-'}${qtd}</td>
            <td class="saldo">${escaparHtml(mov.quantidadeAnterior ?? '-')} → ${escaparHtml(mov.quantidadePosterior ?? '-')}</td>
            <td>${escaparHtml(formatarDataHora(mov.dataMovimentacao))}</td>
        `;
        tbody.appendChild(tr);
    });
}

// Mostra as tintas abaixo do mínimo no quadro de alertas
function renderizarAlertas(alertas) {
    const vazio = document.getElementById('alerta-mensagem');
    const lista = document.getElementById('lista-alertas');
    if (!vazio || !lista) return;

    vazio.hidden = alertas.length > 0;
    lista.hidden = alertas.length === 0;
    lista.innerHTML = '';

    alertas.forEach(item => {
        const cor = corValida(item.corHexadecimal);
        const situacao = situacaoEstoque(Number(item.quantidade));

        const li = document.createElement('li');
        li.innerHTML = `
            <div class="alerta-cor" ${cor ? `style="background-color: ${cor};"` : ''}></div>
            <div class="alerta-info">
                <span class="nome">${escaparHtml(item.nomeTinta || 'Tinta sem nome')}</span>
                <span class="codigo">${escaparHtml(item.codigoTinta || 'Sem código')}</span>
            </div>
            <span class="badge ${situacao.classe}">${escaparHtml(item.quantidade)} un.</span>
        `;
        lista.appendChild(li);
    });
}

// Utilitários das telas de Movimentações, Mistura, Histórico e Financeiro
// GET que devolve o status junto, para cada tela mostrar a mensagem certa (sem permissão, erro...)
async function buscarApi(url) {
    try {
        const token = localStorage.getItem('userToken');
        const response = await fetch(url, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const dados = response.ok ? await response.json() : await response.json().catch(() => ({}));
        return { ok: response.ok, status: response.status, dados };
    } catch (error) {
        console.error(`Erro ao buscar ${url}`, error);
        return { ok: false, status: 0, dados: {} };
    }
}

// POST com JSON; em erro devolve a mensagem que a API mandou no campo "mensagem"
async function enviarApi(url, corpo) {
    const token = localStorage.getItem('userToken');
    const response = await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(corpo)
    });
    // Algumas rotas devolvem o registro criado (ex.: /v1/tintas); outras respondem sem corpo
    if (response.ok) {
        const dados = await response.json().catch(() => null);
        return { ok: true, status: response.status, dados };
    }
    const erro = await response.json().catch(() => ({}));
    return { ok: false, status: response.status, mensagem: erro.mensagem };
}

function mensagemDeErroApi(resultado, padrao) {
    if (resultado.status === 401 || resultado.status === 403) {
        return resultado.mensagem || 'Você não tem permissão para esta ação.';
    }
    // 500 sem mensagem: erro interno (ex.: regra do banco); o detalhe fica no log da API
    if (resultado.status >= 500 && !resultado.mensagem) {
        return `erro interno no servidor (HTTP ${resultado.status}); veja o log da API`;
    }
    return resultado.mensagem || padrao;
}

// Datas sem hora (dataProducao, dataReferencia) chegam em UTC; formatar em UTC evita mostrar o dia anterior
function formatarDataUTC(valor) {
    if (!valor) return '-';
    const data = new Date(valor);
    return isNaN(data) ? '-' : data.toLocaleDateString('pt-BR', { timeZone: 'UTC' });
}

function formatarMoeda(valor) {
    return Number(valor || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

// Data de hoje no formato do <input type="date"> (yyyy-mm-dd), no fuso local
function hojeISO() {
    const hoje = new Date();
    const mes = String(hoje.getMonth() + 1).padStart(2, '0');
    const dia = String(hoje.getDate()).padStart(2, '0');
    return `${hoje.getFullYear()}-${mes}-${dia}`;
}

function iniciaisDe(nome, padrao) {
    return nome
        ? nome.split(' ').filter(Boolean).map(n => n[0]).join('').substring(0, 2).toUpperCase()
        : padrao;
}

// Avatar redondo: amostra da cor quando existe, senão as iniciais
function avatarTinta(cor, nome, classeExtra = '') {
    const corOk = corValida(cor);
    return corOk
        ? `<div class="user-avatar-table cor-tinta ${classeExtra}" style="background-color: ${corOk};" title="${corOk}"></div>`
        : `<div class="user-avatar-table ${classeExtra}">${escaparHtml(iniciaisDe(nome, 'TI'))}</div>`;
}

function ativarFechamentoModal(idModal, fechar) {
    const modal = document.getElementById(idModal);
    if (!modal) return;
    modal.addEventListener('click', (e) => {
        if (e.target === modal) fechar();
    });
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && !modal.hidden) fechar();
    });
}

function mostrarErroModal(idCaixa, texto) {
    const caixa = document.getElementById(idCaixa);
    if (!caixa) return;
    caixa.textContent = texto;
    caixa.hidden = !texto;
}

// Tela de Movimentações
let movimentacoesPagina = [];
let coresPorEstoque = new Map();

async function carregarPaginaMovimentacoes() {
    const tbody = document.getElementById('tabela-lista-movimentacoes-body');
    if (!tbody) return;

    const [resposta, estoques] = await Promise.all([
        buscarApi('/v1/movimentacao-estoque'),
        buscarLista('/v1/estoques')
    ]);

    if (!resposta.ok) {
        const texto = resposta.status === 401 || resposta.status === 403
            ? 'Você não tem permissão para ver as movimentações.'
            : 'Erro ao carregar as movimentações do banco de dados.';
        mensagemNaTabela(tbody, texto, '#ef4444', 7);
        return;
    }

    movimentacoesPagina = resposta.dados;
    coresPorEstoque = new Map(estoques.map(e => [e.idEstoque, e.corHexadecimal]));

    // Resumo considera todas as movimentações, sem filtro
    let entradas = 0;
    let saidas = 0;
    movimentacoesPagina.forEach(mov => {
        const qtd = Number(mov.quantidadeMovimentada) || 0;
        if (mov.tipoMovimentacao === 'ENTRADA') entradas += qtd;
        if (mov.tipoMovimentacao === 'SAIDA') saidas += qtd;
    });
    document.getElementById('mov-total').textContent = movimentacoesPagina.length;
    document.getElementById('mov-total-entradas').textContent = entradas;
    document.getElementById('mov-total-saidas').textContent = saidas;

    renderizarListaMovimentacoes();
}

function renderizarListaMovimentacoes() {
    const tbody = document.getElementById('tabela-lista-movimentacoes-body');
    if (!tbody) return;

    const tipo = document.getElementById('filtro-tipo-movimentacao').value;
    const termo = document.getElementById('busca-movimentacoes').value.trim().toLowerCase();

    const filtradas = movimentacoesPagina
        .filter(mov => !tipo || mov.tipoMovimentacao === tipo)
        .filter(mov => {
            if (!termo) return true;
            const texto = [mov.nomeTinta, mov.codigoTinta, NOMES_MOTIVOS[mov.motivo], mov.nomeFuncionario, mov.observacao]
                .join(' ').toLowerCase();
            return texto.includes(termo);
        })
        .sort((a, b) => new Date(b.dataMovimentacao) - new Date(a.dataMovimentacao));

    if (filtradas.length === 0) {
        const texto = movimentacoesPagina.length === 0 ? 'Nenhuma movimentação registrada.' : 'Nenhuma movimentação encontrada com esse filtro.';
        mensagemNaTabela(tbody, texto, '#64748b', 7);
        return;
    }

    tbody.innerHTML = '';
    filtradas.forEach(mov => {
        const ehEntrada = mov.tipoMovimentacao === 'ENTRADA';
        const qtd = Number(mov.quantidadeMovimentada) || 0;
        const observacao = mov.observacao
            ? `<div class="texto-secundario">${escaparHtml(mov.observacao)}</div>`
            : '';

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>
                <div class="user-cell">
                    ${avatarTinta(coresPorEstoque.get(mov.estoqueId), mov.nomeTinta)}
                    <div class="user-info-text">
                        <span class="name">${escaparHtml(mov.nomeTinta || 'Tinta removida')}</span>
                        <span class="email">${escaparHtml(mov.codigoTinta || '-')}</span>
                    </div>
                </div>
            </td>
            <td><span class="badge ${ehEntrada ? 'badge-active' : 'badge-inactive'}">${ehEntrada ? 'Entrada' : 'Saída'}</span></td>
            <td>${escaparHtml(NOMES_MOTIVOS[mov.motivo] || mov.motivo || '-')}${observacao}</td>
            <td class="nowrap ${ehEntrada ? 'valor-positivo' : 'valor-negativo'}">${ehEntrada ? '+' : '-'}${qtd}</td>
            <td class="nowrap texto-secundario">${escaparHtml(mov.quantidadeAnterior ?? '-')} → ${escaparHtml(mov.quantidadePosterior ?? '-')}</td>
            <td>${escaparHtml(mov.nomeFuncionario || '-')}</td>
            <td class="nowrap">${escaparHtml(formatarDataHora(mov.dataMovimentacao))}</td>
        `;
        tbody.appendChild(tr);
    });
}

function ativarFiltrosMovimentacoes() {
    const busca = document.getElementById('busca-movimentacoes');
    const filtro = document.getElementById('filtro-tipo-movimentacao');
    if (busca) busca.addEventListener('input', renderizarListaMovimentacoes);
    if (filtro) filtro.addEventListener('change', renderizarListaMovimentacoes);
}

// Tela de Mistura
let receitasCarregadas = [];
let itensReceitaCarregados = [];
let tintasMistura = [];
let estoquesMistura = [];
let producoesMistura = [];
let receitaEmProducao = null;

function itensDaReceita(idReceita) {
    return itensReceitaCarregados.filter(i => Number(i.receitaRefId) === Number(idReceita));
}

function somaProporcoes(itens) {
    // Soma em centésimos para evitar erro de arredondamento (ex.: 33.33 + 66.67)
    const centesimos = itens.reduce((total, i) => total + Math.round(Number(i.proporcaoPercentual) * 100), 0);
    return centesimos / 100;
}

function tintaPorId(id) {
    return tintasMistura.find(t => Number(t.id) === Number(id));
}

async function carregarReceitas() {
    const tbody = document.getElementById('tabela-receitas-body');
    if (!tbody) return;

    const [resposta, itens, tintas, estoques, producoes] = await Promise.all([
        buscarApi('/v1/receitas'),
        buscarLista('/v1/itens-receita'),
        buscarLista('/v1/tintas'),
        buscarLista('/v1/estoques'),
        buscarLista('/v1/producoes')
    ]);

    // As tintas vêm antes da checagem das receitas para o simulador funcionar mesmo sem acesso às receitas
    tintasMistura = tintas;
    estoquesMistura = estoques;
    iniciarSimulador();

    if (!resposta.ok) {
        const texto = resposta.status === 401 || resposta.status === 403
            ? 'Você não tem permissão para ver as receitas.'
            : 'Erro ao carregar as receitas do banco de dados.';
        mensagemNaTabela(tbody, texto, '#ef4444');
        return;
    }

    receitasCarregadas = resposta.dados;
    itensReceitaCarregados = itens;
    producoesMistura = producoes;

    if (receitasCarregadas.length === 0) {
        mensagemNaTabela(tbody, 'Nenhuma receita cadastrada. Clique em "Nova Receita" para começar.', '#64748b');
        return;
    }

    tbody.innerHTML = '';
    [...receitasCarregadas]
        .sort((a, b) => String(a.nome).localeCompare(String(b.nome)))
        .forEach(receita => {
            const itensReceita = itensDaReceita(receita.id);
            const soma = somaProporcoes(itensReceita);
            const pronta = soma === 100;
            const resultante = tintaPorId(receita.tintaResultanteId);
            const qtdProducoes = producoesMistura.filter(p => Number(p.receitaRefId) === Number(receita.id)).length;

            const chips = itensReceita.length
                ? itensReceita.map(item => {
                    const tinta = tintaPorId(item.tintaMateriaPrimaRefId);
                    const cor = corValida(tinta && tinta.numeroHexadecimal);
                    return `<span class="chip"><span class="bolinha" ${cor ? `style="background-color: ${cor};"` : ''}></span>${escaparHtml(tinta ? tinta.nome : 'Tinta removida')} · ${escaparHtml(Number(item.proporcaoPercentual))}%</span>`;
                }).join('')
                : '<span class="texto-secundario">Sem ingredientes</span>';

            const situacao = pronta
                ? '<span class="badge badge-active">Pronta</span>'
                : `<span class="badge badge-warning">Incompleta (${soma}%)</span>`;

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>
                    <div class="user-cell">
                        ${avatarTinta(resultante && resultante.numeroHexadecimal, receita.nome)}
                        <div class="user-info-text">
                            <span class="name">${escaparHtml(receita.nome)}</span>
                            <span class="email">Gera: ${escaparHtml(receita.tintaResultanteNome || (resultante && resultante.nome) || '-')}</span>
                        </div>
                    </div>
                </td>
                <td><div class="chips">${chips}</div></td>
                <td class="nowrap">${formatarMoeda(receita.valorPorLitro)}</td>
                <td>${situacao}</td>
                <td>${qtdProducoes}</td>
                <td>
                    <div class="action-buttons" style="justify-content: flex-end;">
                        <button class="btn-icon" title="${pronta ? 'Produzir com esta receita' : 'A receita precisa somar 100% para produzir'}"
                            onclick="abrirModalProducao(${Number(receita.id)})" ${pronta ? '' : 'disabled style="opacity: 0.4; cursor: not-allowed;"'}>
                            <i data-lucide="flask-conical"></i>
                        </button>
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        });

    if (window.lucide) lucide.createIcons();

    const busca = document.querySelector('.search-container input');
    if (busca && busca.value) busca.dispatchEvent(new Event('input'));
}

// Opções de tinta ativa para os selects da receita
function opcoesDeTintas(placeholder) {
    const ativas = tintasMistura.filter(t => t.ativo).sort((a, b) => String(a.nome).localeCompare(String(b.nome)));
    return `<option value="">${placeholder}</option>` + ativas
        .map(t => `<option value="${Number(t.id)}">${escaparHtml(t.codigo ? `${t.nome} (${t.codigo})` : t.nome)}</option>`)
        .join('');
}

function abrirModalReceita() {
    const modal = document.getElementById('modal-receita');
    if (!modal) return;

    if (tintasMistura.filter(t => t.ativo).length < 2) {
        alert('Cadastre pelo menos duas tintas ativas para montar uma receita.');
        return;
    }

    document.getElementById('form-receita').reset();
    mostrarErroModal('modal-receita-erro', '');
    document.getElementById('receita-tinta').innerHTML = opcoesDeTintas('Selecione a tinta que será produzida');
    document.getElementById('lista-ingredientes').innerHTML = '';
    adicionarIngrediente();
    adicionarIngrediente();
    atualizarSomaProporcoes();

    modal.hidden = false;
    document.getElementById('receita-nome').focus();
}

function fecharModalReceita() {
    const modal = document.getElementById('modal-receita');
    if (modal) modal.hidden = true;
}

function adicionarIngrediente() {
    const lista = document.getElementById('lista-ingredientes');
    const linha = document.createElement('div');
    linha.className = 'ingrediente-linha';
    linha.innerHTML = `
        <select class="ingrediente-tinta" aria-label="Tinta do ingrediente">${opcoesDeTintas('Selecione a tinta')}</select>
        <input type="number" class="ingrediente-proporcao" min="0.01" max="100" step="0.01" placeholder="%" aria-label="Proporção em %">
        <button type="button" class="btn-icon danger" title="Remover ingrediente">
            <i data-lucide="x"></i>
        </button>
    `;
    linha.querySelector('.ingrediente-proporcao').addEventListener('input', atualizarSomaProporcoes);
    linha.querySelector('.ingrediente-tinta').addEventListener('change', atualizarSomaProporcoes);
    linha.querySelector('button').addEventListener('click', () => {
        linha.remove();
        atualizarSomaProporcoes();
    });
    lista.appendChild(linha);
    if (window.lucide) lucide.createIcons();
}

function lerIngredientes() {
    return [...document.querySelectorAll('#lista-ingredientes .ingrediente-linha')].map(linha => ({
        idTinta: linha.querySelector('.ingrediente-tinta').value,
        proporcao: Number(linha.querySelector('.ingrediente-proporcao').value)
    }));
}

function atualizarSomaProporcoes() {
    const soma = somaProporcoes(lerIngredientes().map(i => ({ proporcaoPercentual: i.proporcao || 0 })));
    const campo = document.getElementById('soma-proporcoes');
    campo.textContent = `${soma}%`;
    campo.className = soma === 100 ? 'ok' : 'erro';
    atualizarPreviaReceita();
}

// Prévia da cor no modal de Nova Receita
function atualizarPreviaReceita() {
    const componentes = lerIngredientes().map(i => {
        const tinta = tintaPorId(i.idTinta);
        return { hex: tinta && tinta.numeroHexadecimal, peso: i.proporcao || 0 };
    });
    pintarPrevia('receita-previa-cor', 'receita-previa-hex', misturarCores(componentes));
}

function ativarFormularioReceita() {
    const form = document.getElementById('form-receita');
    if (!form) return;
    ativarFechamentoModal('modal-receita', fecharModalReceita);
    form.addEventListener('submit', salvarReceita);
}

async function salvarReceita(event) {
    event.preventDefault();

    const nome = document.getElementById('receita-nome').value.trim();
    const idResultante = document.getElementById('receita-tinta').value;
    const textoValor = document.getElementById('receita-valor').value.trim();
    const valor = Number(textoValor);
    const ingredientes = lerIngredientes();

    if (!nome || !idResultante || textoValor === '') {
        mostrarErroModal('modal-receita-erro', 'Preencha o nome, a tinta resultante e o valor por litro.');
        return;
    }
    if (isNaN(valor) || valor < 0) {
        mostrarErroModal('modal-receita-erro', 'O valor por litro precisa ser zero ou maior.');
        return;
    }
    if (ingredientes.length === 0 || ingredientes.some(i => !i.idTinta || !(i.proporcao > 0))) {
        mostrarErroModal('modal-receita-erro', 'Cada ingrediente precisa de uma tinta e de uma proporção maior que zero.');
        return;
    }
    if (new Set(ingredientes.map(i => i.idTinta)).size !== ingredientes.length) {
        mostrarErroModal('modal-receita-erro', 'A mesma tinta aparece em mais de um ingrediente.');
        return;
    }
    if (ingredientes.some(i => i.idTinta === idResultante)) {
        mostrarErroModal('modal-receita-erro', 'A tinta resultante não pode ser ingrediente da própria receita.');
        return;
    }
    if (somaProporcoes(ingredientes.map(i => ({ proporcaoPercentual: i.proporcao }))) !== 100) {
        mostrarErroModal('modal-receita-erro', 'A soma das proporções precisa ser exatamente 100%.');
        return;
    }

    const botao = document.getElementById('btn-salvar-receita');
    botao.disabled = true;
    mostrarErroModal('modal-receita-erro', '');

    try {
        const resultado = await criarReceitaComIngredientes(nome, idResultante, valor, ingredientes);
        if (!resultado.ok) {
            mostrarErroModal('modal-receita-erro', resultado.mensagem);
            if (resultado.receitaCriada) carregarReceitas();
            return;
        }

        fecharModalReceita();
        carregarReceitas();
    } catch (error) {
        console.error('Erro ao salvar receita:', error);
        mostrarErroModal('modal-receita-erro', 'Não foi possível salvar a receita.');
    } finally {
        botao.disabled = false;
    }
}

// Cria a receita e depois cada ingrediente. Usado pelo modal e pelo simulador.
// ingredientes: [{ idTinta, proporcao }]. Devolve { ok, mensagem, receitaCriada }.
async function criarReceitaComIngredientes(nome, idResultante, valor, ingredientes) {
    const criada = await enviarApi('/v1/receitas', {
        nome,
        idTintaResultante: Number(idResultante),
        valorPorLitro: valor
    });
    if (!criada.ok) {
        return { ok: false, mensagem: mensagemDeErroApi(criada, 'Não foi possível criar a receita.') };
    }

    // O POST não devolve o id; busca a receita recém-criada pelo nome e tinta resultante
    const lista = await buscarApi('/v1/receitas');
    const candidatas = (lista.ok ? lista.dados : [])
        .filter(r => r.nome === nome && Number(r.tintaResultanteId) === Number(idResultante));
    const receita = candidatas.sort((a, b) => Number(b.id) - Number(a.id))[0];
    if (!receita) {
        return { ok: false, receitaCriada: true, mensagem: 'A receita foi criada, mas não foi possível localizá-la para adicionar os ingredientes.' };
    }

    for (const ingrediente of ingredientes) {
        const item = await enviarApi('/v1/itens-receita', {
            idReceita: Number(receita.id),
            idTintaMateriaPrima: Number(ingrediente.idTinta),
            proporcaoPercentual: ingrediente.proporcao
        });
        if (!item.ok) {
            const tinta = tintaPorId(ingrediente.idTinta);
            return {
                ok: false,
                receitaCriada: true,
                mensagem: `A receita foi criada, mas o ingrediente ${tinta ? tinta.nome : ''} não foi salvo: ${mensagemDeErroApi(item, 'erro desconhecido')}`
            };
        }
    }

    return { ok: true, receitaCriada: true };
}

// Produção: calcula quanto de cada ingrediente será consumido e confere o estoque
function abrirModalProducao(idReceita) {
    const receita = receitasCarregadas.find(r => Number(r.id) === idReceita);
    const modal = document.getElementById('modal-producao');
    if (!receita || !modal) return;

    receitaEmProducao = receita;
    document.getElementById('form-producao').reset();
    mostrarErroModal('modal-producao-erro', '');
    document.getElementById('producao-data').value = hojeISO();

    const resultante = tintaPorId(receita.tintaResultanteId);
    document.getElementById('producao-resumo').innerHTML =
        `Receita <strong>${escaparHtml(receita.nome)}</strong> → gera <strong>${escaparHtml(receita.tintaResultanteNome || (resultante && resultante.nome) || '-')}</strong>.
         As quantidades abaixo serão retiradas do estoque e o volume produzido entrará como nova tinta.`;

    atualizarDosagem();
    modal.hidden = false;
    document.getElementById('producao-volume').focus();
}

function fecharModalProducao() {
    const modal = document.getElementById('modal-producao');
    if (modal) modal.hidden = true;
    receitaEmProducao = null;
}

function atualizarDosagem() {
    if (!receitaEmProducao) return;
    const tbody = document.getElementById('tabela-dosagem-body');
    const volume = Number(document.getElementById('producao-volume').value) || 0;
    let falta = false;

    tbody.innerHTML = '';
    itensDaReceita(receitaEmProducao.id).forEach(item => {
        const tinta = tintaPorId(item.tintaMateriaPrimaRefId);
        const estoque = estoquesMistura.find(e => Number(e.idTinta) === Number(item.tintaMateriaPrimaRefId));
        const disponivel = estoque ? Number(estoque.quantidade) : 0;
        // Mesmo cálculo do backend: proporção × volume ÷ 100, arredondado
        const necessario = Math.round(Number(item.proporcaoPercentual) * volume / 100);
        const suficiente = disponivel >= necessario;
        if (!suficiente) falta = true;

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${escaparHtml(tinta ? tinta.nome : 'Tinta removida')}</td>
            <td>${escaparHtml(Number(item.proporcaoPercentual))}%</td>
            <td><strong>${necessario} mL</strong></td>
            <td>${disponivel} mL</td>
            <td><span class="badge ${suficiente ? 'badge-active' : 'badge-inactive'}">${suficiente ? 'OK' : 'Insuficiente'}</span></td>
        `;
        tbody.appendChild(tr);
    });

    document.getElementById('btn-salvar-producao').disabled = volume <= 0 || falta;
}

function ativarFormularioProducao() {
    const form = document.getElementById('form-producao');
    if (!form) return;
    ativarFechamentoModal('modal-producao', fecharModalProducao);
    document.getElementById('producao-volume').addEventListener('input', atualizarDosagem);
    form.addEventListener('submit', salvarProducao);
}

async function salvarProducao(event) {
    event.preventDefault();
    if (!receitaEmProducao) return;

    const volume = Number(document.getElementById('producao-volume').value);
    const data = document.getElementById('producao-data').value;

    if (!Number.isInteger(volume) || volume <= 0) {
        mostrarErroModal('modal-producao-erro', 'O volume precisa ser um número inteiro maior que zero.');
        return;
    }
    if (!data) {
        mostrarErroModal('modal-producao-erro', 'Informe a data da produção.');
        return;
    }

    const botao = document.getElementById('btn-salvar-producao');
    botao.disabled = true;
    mostrarErroModal('modal-producao-erro', '');

    try {
        const resultado = await enviarApi('/v1/producoes', {
            idReceita: Number(receitaEmProducao.id),
            volumeProduzido: volume,
            dataProducao: data
        });
        if (!resultado.ok) {
            mostrarErroModal('modal-producao-erro', mensagemDeErroApi(resultado, 'Não foi possível registrar a produção.'));
            botao.disabled = false;
            return;
        }

        fecharModalProducao();
        carregarReceitas();
    } catch (error) {
        console.error('Erro ao registrar produção:', error);
        mostrarErroModal('modal-producao-erro', 'Não foi possível registrar a produção.');
        botao.disabled = false;
    }
}

// Tela de Histórico
let atividadesHistorico = [];
let acessosCarregados = false;

const ACOES_HISTORICO = {
    ENTRADA: { texto: 'Entrada', classe: 'badge-active' },
    SAIDA: { texto: 'Saída', classe: 'badge-inactive' },
    PRODUCAO: { texto: 'Produção', classe: 'badge-purple' },
    RECEITA: { texto: 'Receita', classe: 'badge-info' }
};

async function carregarHistorico() {
    const tbody = document.getElementById('tabela-atividades-body');
    if (!tbody) return;

    const [movimentacoes, producoes, receitas] = await Promise.all([
        buscarLista('/v1/movimentacao-estoque'),
        buscarLista('/v1/producoes'),
        buscarLista('/v1/receitas')
    ]);

    // A produção não traz o nome do funcionário; as movimentações geradas por ela trazem
    const responsavelPorProducao = new Map();
    movimentacoes.forEach(m => {
        if (m.producaoId && m.nomeFuncionario) responsavelPorProducao.set(m.producaoId, m.nomeFuncionario);
    });
    const receitaPorId = new Map(receitas.map(r => [r.id, r]));

    atividadesHistorico = [
        ...movimentacoes.map(m => {
            const motivo = NOMES_MOTIVOS[m.motivo] || m.motivo;
            const obs = m.observacao ? ` — "${m.observacao}"` : '';
            return {
                data: m.dataMovimentacao,
                tipo: m.tipoMovimentacao,
                descricao: `${motivo}: ${m.quantidadeMovimentada} de ${m.nomeTinta || 'tinta removida'} (saldo ${m.quantidadeAnterior} → ${m.quantidadePosterior})${obs}`,
                responsavel: m.nomeFuncionario
            };
        }),
        ...producoes.map(p => {
            const receita = receitaPorId.get(p.receitaRefId);
            return {
                data: p.criadoEm || p.dataProducao,
                tipo: 'PRODUCAO',
                descricao: `Produção de ${p.volumeProduzido} mL de ${receita ? receita.tintaResultanteNome : 'tinta'} pela receita "${receita ? receita.nome : 'removida'}" (data ${formatarDataUTC(p.dataProducao)})`,
                responsavel: responsavelPorProducao.get(p.id)
            };
        }),
        ...receitas.map(r => ({
            data: r.criadoEm,
            tipo: 'RECEITA',
            descricao: `Receita "${r.nome}" criada para gerar ${r.tintaResultanteNome || 'tinta'}`,
            responsavel: r.criadoPorNome
        }))
    ].sort((a, b) => new Date(b.data) - new Date(a.data));

    renderizarHistorico();
}

function renderizarHistorico() {
    const tbody = document.getElementById('tabela-atividades-body');
    if (!tbody) return;

    const tipo = document.getElementById('filtro-tipo-atividade').value;
    const termo = document.getElementById('busca-historico').value.trim().toLowerCase();

    const filtradas = atividadesHistorico
        .filter(a => !tipo || a.tipo === tipo)
        .filter(a => !termo || `${a.descricao} ${a.responsavel || ''}`.toLowerCase().includes(termo));

    if (filtradas.length === 0) {
        const texto = atividadesHistorico.length === 0 ? 'Nenhuma atividade registrada.' : 'Nenhuma atividade encontrada com esse filtro.';
        mensagemNaTabela(tbody, texto, '#64748b', 4);
        return;
    }

    tbody.innerHTML = '';
    filtradas.forEach(a => {
        const acao = ACOES_HISTORICO[a.tipo] || { texto: a.tipo, classe: 'badge-role' };
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="nowrap">${escaparHtml(formatarDataHora(a.data))}</td>
            <td><span class="badge ${acao.classe}">${acao.texto}</span></td>
            <td>${escaparHtml(a.descricao)}</td>
            <td>${escaparHtml(a.responsavel || '-')}</td>
        `;
        tbody.appendChild(tr);
    });
}

async function carregarAcessos() {
    const tbody = document.getElementById('tabela-acessos-body');
    if (!tbody) return;
    acessosCarregados = true;
    mensagemNaTabela(tbody, 'Carregando acessos...', '#64748b', 4);

    const resposta = await buscarApi('/v1/log-acessos');
    if (!resposta.ok) {
        const texto = resposta.status === 401 || resposta.status === 403
            ? 'Somente usuários ADMIN podem ver o log de acessos.'
            : 'Erro ao carregar o log de acessos.';
        mensagemNaTabela(tbody, texto, '#ef4444', 4);
        return;
    }
    if (resposta.dados.length === 0) {
        mensagemNaTabela(tbody, 'Nenhum acesso registrado.', '#64748b', 4);
        return;
    }

    tbody.innerHTML = '';
    [...resposta.dados]
        .sort((a, b) => new Date(b.datahora) - new Date(a.datahora))
        .forEach(log => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="nowrap">${escaparHtml(formatarDataHora(log.datahora))}</td>
                <td>${escaparHtml(log.emailUsado || '-')}</td>
                <td><span class="badge ${log.sucesso ? 'badge-active' : 'badge-inactive'}">${log.sucesso ? 'Sucesso' : 'Falha'}</span></td>
                <td>${escaparHtml(log.ipOrigem || '-')}</td>
            `;
            tbody.appendChild(tr);
        });
}

function ativarFiltrosHistorico() {
    document.getElementById('busca-historico').addEventListener('input', renderizarHistorico);
    document.getElementById('filtro-tipo-atividade').addEventListener('change', renderizarHistorico);

    // Troca de abas; os acessos só são buscados na primeira vez que a aba é aberta
    document.querySelectorAll('.aba').forEach(botao => {
        botao.addEventListener('click', () => {
            const aba = botao.dataset.aba;
            document.querySelectorAll('.aba').forEach(b => b.classList.toggle('ativa', b === botao));
            document.getElementById('painel-atividades').hidden = aba !== 'atividades';
            document.getElementById('painel-acessos').hidden = aba !== 'acessos';
            if (aba === 'acessos' && !acessosCarregados) carregarAcessos();
        });
    });
}

// Tela de Financeiro
async function carregarFinanceiro() {
    const tbody = document.getElementById('tabela-financeiro-body');
    if (!tbody) return;

    const resposta = await buscarApi('/v1/financeiro');
    if (!resposta.ok) {
        const texto = resposta.status === 401 || resposta.status === 403
            ? 'Você não tem permissão para ver o financeiro (é preciso nível GERENTE ou ADMIN).'
            : 'Erro ao carregar o financeiro do banco de dados.';
        mensagemNaTabela(tbody, texto, '#ef4444');
        return;
    }

    const fechamentos = [...resposta.dados].sort((a, b) => new Date(b.dataReferencia) - new Date(a.dataReferencia));

    const totais = fechamentos.reduce((t, f) => ({
        receita: t.receita + Number(f.receitaTotal || 0),
        compras: t.compras + Number(f.compras || 0),
        perdas: t.perdas + Number(f.perdas || 0),
        saldo: t.saldo + Number(f.saldoLiquido || 0)
    }), { receita: 0, compras: 0, perdas: 0, saldo: 0 });

    document.getElementById('fin-receita').textContent = formatarMoeda(totais.receita);
    document.getElementById('fin-compras').textContent = formatarMoeda(totais.compras);
    document.getElementById('fin-perdas').textContent = formatarMoeda(totais.perdas);
    const saldo = document.getElementById('fin-saldo');
    saldo.textContent = formatarMoeda(totais.saldo);
    saldo.className = `stat-value ${totais.saldo < 0 ? 'valor-negativo' : 'valor-positivo'}`;

    if (fechamentos.length === 0) {
        mensagemNaTabela(tbody, 'Nenhum fechamento registrado.', '#64748b');
        return;
    }

    tbody.innerHTML = '';
    fechamentos.forEach(f => {
        const saldoLinha = Number(f.saldoLiquido || 0);
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="nowrap"><strong>${escaparHtml(formatarDataUTC(f.dataReferencia))}</strong></td>
            <td class="nowrap">${formatarMoeda(f.receitaTotal)}</td>
            <td class="nowrap">${formatarMoeda(f.compras)}</td>
            <td class="nowrap">${formatarMoeda(f.perdas)}</td>
            <td class="nowrap ${saldoLinha < 0 ? 'valor-negativo' : 'valor-positivo'}">${formatarMoeda(saldoLinha)}</td>
            <td class="nowrap">${escaparHtml(formatarData(f.criadoEm))}</td>
        `;
        tbody.appendChild(tr);
    });

    const busca = document.querySelector('.search-container input');
    if (busca && busca.value) busca.dispatchEvent(new Event('input'));
}

function abrirModalFinanceiro() {
    const modal = document.getElementById('modal-financeiro');
    if (!modal) return;
    document.getElementById('form-financeiro').reset();
    mostrarErroModal('modal-financeiro-erro', '');
    document.getElementById('fin-data').value = hojeISO();
    atualizarPreviaFinanceiro();
    modal.hidden = false;
    document.getElementById('fin-valor-receita').focus();
}

function fecharModalFinanceiro() {
    const modal = document.getElementById('modal-financeiro');
    if (modal) modal.hidden = true;
}

function lerValoresFinanceiro() {
    const ler = (id) => document.getElementById(id).value.trim();
    return {
        receita: ler('fin-valor-receita'),
        compras: ler('fin-valor-compras'),
        perdas: ler('fin-valor-perdas')
    };
}

function atualizarPreviaFinanceiro() {
    const v = lerValoresFinanceiro();
    const saldo = (Number(v.receita) || 0) - (Number(v.compras) || 0) - (Number(v.perdas) || 0);
    const campo = document.getElementById('fin-previa-saldo');
    campo.textContent = formatarMoeda(saldo);
    campo.className = saldo < 0 ? 'valor-negativo' : 'valor-positivo';
}

function ativarFormularioFinanceiro() {
    const form = document.getElementById('form-financeiro');
    if (!form) return;
    ativarFechamentoModal('modal-financeiro', fecharModalFinanceiro);
    ['fin-valor-receita', 'fin-valor-compras', 'fin-valor-perdas'].forEach(id =>
        document.getElementById(id).addEventListener('input', atualizarPreviaFinanceiro));
    form.addEventListener('submit', salvarFinanceiro);
}

async function salvarFinanceiro(event) {
    event.preventDefault();

    const data = document.getElementById('fin-data').value;
    const v = lerValoresFinanceiro();
    const valores = [v.receita, v.compras, v.perdas];

    if (!data || valores.some(x => x === '')) {
        mostrarErroModal('modal-financeiro-erro', 'Preencha a data e os três valores (use 0 quando não houver).');
        return;
    }
    if (valores.some(x => isNaN(Number(x)) || Number(x) < 0)) {
        mostrarErroModal('modal-financeiro-erro', 'Os valores precisam ser zero ou maiores.');
        return;
    }

    const botao = document.getElementById('btn-salvar-financeiro');
    botao.disabled = true;
    mostrarErroModal('modal-financeiro-erro', '');

    try {
        const resultado = await enviarApi('/v1/financeiro', {
            receitaTotal: Number(v.receita),
            compras: Number(v.compras),
            perdas: Number(v.perdas),
            dataReferencia: data
        });
        if (!resultado.ok) {
            mostrarErroModal('modal-financeiro-erro', mensagemDeErroApi(resultado, 'Não foi possível salvar o fechamento.'));
            return;
        }
        fecharModalFinanceiro();
        carregarFinanceiro();
    } catch (error) {
        console.error('Erro ao salvar fechamento:', error);
        mostrarErroModal('modal-financeiro-erro', 'Não foi possível salvar o fechamento.');
    } finally {
        botao.disabled = false;
    }
}

// Simulador de Mistura (tela de Mistura)
const MAX_PAINEIS_SIMULADOR = 5;

// Quanto da mistura "subtrativa" entra no resultado (o resto é a média simples das cores)
const PESO_MISTURA_SUBTRATIVA = 0.65;

// Mistura cores como pigmento: média geométrica ponderada de cada canal (imita a tinta
// absorvendo luz, ex.: amarelo + azul = verde), combinada com a média simples para não escurecer demais.
// componentes: [{ hex: '#RRGGBB', peso: número }]. Devolve '#RRGGBB' ou null se não houver cor válida.
function misturarCores(componentes) {
    const validos = componentes
        .map(c => ({ hex: corValida(c.hex), peso: Number(c.peso) || 0 }))
        .filter(c => c.hex && c.peso > 0);
    if (validos.length === 0) return null;

    const pesoTotal = validos.reduce((t, c) => t + c.peso, 0);
    const canais = [0, 1, 2].map(i => {
        let geometrica = 1;
        let linear = 0;
        validos.forEach(c => {
            const valor = parseInt(c.hex.substr(1 + i * 2, 2), 16) / 255;
            const peso = c.peso / pesoTotal;
            // Piso evita que um canal zerado (ex.: vermelho puro) apague a mistura inteira
            geometrica *= Math.pow(Math.max(valor, 0.04), peso);
            linear += valor * peso;
        });
        const resultado = PESO_MISTURA_SUBTRATIVA * geometrica + (1 - PESO_MISTURA_SUBTRATIVA) * linear;
        return Math.round(Math.min(1, resultado) * 255);
    });

    return '#' + canais.map(v => v.toString(16).padStart(2, '0')).join('').toUpperCase();
}

// Pinta o quadrado de prévia (ou volta ao xadrez de "sem cor")
function pintarPrevia(idCor, idHex, hex) {
    const quadrado = document.getElementById(idCor);
    const texto = document.getElementById(idHex);
    if (!quadrado || !texto) return;

    quadrado.style.backgroundColor = hex || '';
    quadrado.classList.toggle('com-cor', Boolean(hex));
    texto.textContent = hex || '—';
}

function opcoesDoSimulador() {
    const ativas = tintasMistura.filter(t => t.ativo).sort((a, b) => String(a.nome).localeCompare(String(b.nome)));
    return '<option value="">Escolha uma tinta</option>' + ativas
        .map(t => `<option value="${Number(t.id)}">${escaparHtml(t.nome)}${corValida(t.numeroHexadecimal) ? '' : ' (sem cor)'}</option>`)
        .join('');
}

function iniciarSimulador() {
    const paineis = document.getElementById('simulador-paineis');
    if (!paineis) return;

    // Já iniciado: só atualiza as opções (ex.: tinta recém-criada) mantendo o que estava escolhido
    if (paineis.children.length > 0) {
        paineis.querySelectorAll('.painel-select').forEach(select => {
            const atual = select.value;
            select.innerHTML = opcoesDoSimulador();
            select.value = atual;
        });
        atualizarSimulador();
        return;
    }

    // Começa com as duas primeiras tintas que têm cor, para a prévia já aparecer
    const comCor = tintasMistura.filter(t => t.ativo && corValida(t.numeroHexadecimal));
    adicionarPainelSimulador(comCor[0] && comCor[0].id);
    adicionarPainelSimulador(comCor[1] && comCor[1].id);
}

function adicionarPainelSimulador(idTinta) {
    const paineis = document.getElementById('simulador-paineis');
    if (!paineis || paineis.children.length >= MAX_PAINEIS_SIMULADOR) return;

    const painel = document.createElement('div');
    painel.className = 'painel-tinta';
    painel.innerHTML = `
        <div class="painel-topo">
            <span class="previa-label">Tinta ${paineis.children.length + 1}</span>
            <button type="button" class="btn-icon danger" title="Remover tinta da mistura">
                <i data-lucide="x"></i>
            </button>
        </div>
        <select class="painel-select" aria-label="Tinta do painel">${opcoesDoSimulador()}</select>
        <div class="painel-quantidade">
            <input type="range" class="painel-range" min="0" max="100" step="1" value="50" aria-label="Quantidade da tinta">
            <span class="painel-pct">—</span>
        </div>
    `;

    const select = painel.querySelector('.painel-select');
    if (idTinta) select.value = String(idTinta);
    select.addEventListener('change', atualizarSimulador);
    painel.querySelector('.painel-range').addEventListener('input', atualizarSimulador);
    painel.querySelector('button').addEventListener('click', () => {
        painel.remove();
        renumerarPaineis();
        atualizarSimulador();
    });

    paineis.appendChild(painel);
    if (window.lucide) lucide.createIcons();
    atualizarSimulador();
}

function renumerarPaineis() {
    document.querySelectorAll('#simulador-paineis .painel-tinta .painel-topo .previa-label')
        .forEach((rotulo, i) => { rotulo.textContent = `Tinta ${i + 1}`; });
}

// Lê os painéis com tinta escolhida; tintas repetidas somam a quantidade
function lerPaineisSimulador() {
    const porTinta = new Map();
    document.querySelectorAll('#simulador-paineis .painel-tinta').forEach(painel => {
        const idTinta = painel.querySelector('.painel-select').value;
        const peso = Number(painel.querySelector('.painel-range').value) || 0;
        if (idTinta) porTinta.set(idTinta, (porTinta.get(idTinta) || 0) + peso);
    });
    return [...porTinta.entries()].map(([idTinta, peso]) => ({ idTinta, peso }));
}

function atualizarSimulador() {
    const paineis = [...document.querySelectorAll('#simulador-paineis .painel-tinta')];
    const selecionados = lerPaineisSimulador();
    const pesoTotal = selecionados.reduce((t, s) => t + s.peso, 0);

    // Cada painel mostra a sua fatia da mistura e ganha a borda da cor escolhida
    paineis.forEach(painel => {
        const idTinta = painel.querySelector('.painel-select').value;
        const peso = Number(painel.querySelector('.painel-range').value) || 0;
        const tinta = tintaPorId(idTinta);
        painel.style.borderTopColor = (tinta && corValida(tinta.numeroHexadecimal)) || '#cbd5e1';
        painel.querySelector('.painel-pct').textContent = idTinta && pesoTotal > 0
            ? `${Math.round(peso / pesoTotal * 1000) / 10}%`
            : '—';
    });

    const hex = misturarCores(selecionados.map(s => {
        const tinta = tintaPorId(s.idTinta);
        return { hex: tinta && tinta.numeroHexadecimal, peso: s.peso };
    }));
    pintarPrevia('simulador-previa-cor', 'simulador-previa-hex', hex);
    document.getElementById('simulador-previa-vazia').hidden = Boolean(hex);

    const composicao = selecionados
        .filter(s => s.peso > 0)
        .map(s => {
            const tinta = tintaPorId(s.idTinta);
            return `${tinta ? tinta.nome : 'Tinta'} ${Math.round(s.peso / pesoTotal * 1000) / 10}%`;
        });
    document.getElementById('simulador-previa-composicao').textContent = composicao.length
        ? composicao.join(' + ')
        : 'Nenhuma tinta selecionada';

    document.getElementById('btn-simulador-receita').disabled = selecionados.filter(s => s.peso > 0).length < 2;
    document.getElementById('simulador-nome').placeholder = nomeSugeridoDoSimulador() || 'Nome sugerido automaticamente';
    document.getElementById('btn-adicionar-painel').hidden = paineis.length >= MAX_PAINEIS_SIMULADOR;
}

function limparSimulador() {
    document.getElementById('simulador-paineis').innerHTML = '';
    adicionarPainelSimulador();
    adicionarPainelSimulador();
}

// Converte os pesos dos painéis em porcentagens com 2 casas que somam exatamente 100
function proporcoesDoSimulador() {
    const selecionados = lerPaineisSimulador().filter(s => s.peso > 0);
    const pesoTotal = selecionados.reduce((t, s) => t + s.peso, 0);
    const centesimos = selecionados.map(s => Math.round(s.peso / pesoTotal * 10000));
    const diferenca = 10000 - centesimos.reduce((t, c) => t + c, 0);
    // A sobra do arredondamento vai para o maior ingrediente
    const maior = centesimos.indexOf(Math.max(...centesimos));
    centesimos[maior] += diferenca;
    return selecionados.map((s, i) => ({ idTinta: s.idTinta, proporcao: centesimos[i] / 100 }));
}

// Nome sugerido a partir dos ingredientes, ex.: "Vermelho Ferrari + Verde Musgo" (máx. 50, limite da receita)
function nomeSugeridoDoSimulador() {
    const nomes = lerPaineisSimulador()
        .filter(s => s.peso > 0)
        .map(s => (tintaPorId(s.idTinta) || {}).nome)
        .filter(Boolean);
    return nomes.join(' + ').substring(0, 50).trim();
}

function mostrarStatusSimulador(html, tipo) {
    const caixa = document.getElementById('simulador-status');
    if (!caixa) return;
    caixa.innerHTML = html;
    caixa.className = `simulador-status ${tipo || ''}`;
    caixa.hidden = !html;
}

// Um clique: cria a tinta com a cor da prévia (o backend já cria o estoque dela),
// cria a receita apontando para essa tinta e salva os ingredientes com as proporções dos painéis
async function salvarMisturaDoSimulador() {
    const proporcoes = proporcoesDoSimulador();
    const hex = document.getElementById('simulador-previa-hex').textContent.trim();

    if (proporcoes.length < 2) {
        mostrarStatusSimulador('Escolha pelo menos duas tintas com quantidade maior que zero.', 'erro');
        return;
    }
    if (!corValida(hex)) {
        mostrarStatusSimulador('Escolha tintas com cor cadastrada para gerar a prévia.', 'erro');
        return;
    }

    const nome = (document.getElementById('simulador-nome').value.trim() || nomeSugeridoDoSimulador()).substring(0, 50);
    const textoValor = document.getElementById('simulador-valor').value.trim();
    const valor = textoValor === '' ? 0 : Number(textoValor);
    if (isNaN(valor) || valor < 0) {
        mostrarStatusSimulador('O valor por litro precisa ser zero ou maior.', 'erro');
        return;
    }

    const botao = document.getElementById('btn-simulador-receita');
    botao.disabled = true;
    mostrarStatusSimulador('Salvando...', '');

    try {
        // O banco não aceita duas tintas com a mesma cor: se já existir, a receita usa a existente
        let tinta = tintasMistura.find(t => String(t.numeroHexadecimal || '').toUpperCase() === hex);
        let reaproveitada = Boolean(tinta);

        if (tinta && proporcoes.some(p => Number(p.idTinta) === Number(tinta.id))) {
            mostrarStatusSimulador(`A cor resultante é igual à de "${escaparHtml(tinta.nome)}", que já é um ingrediente. Ajuste as quantidades.`, 'erro');
            return;
        }

        if (!tinta) {
            const criada = await enviarApi('/v1/tintas', {
                nome,
                numeroHexadecimal: hex,
                codigo: `MIX-${hex.substring(1)}`,
                origem: 'PRODUZIDA'
            });
            if (!criada.ok || !criada.dados) {
                mostrarStatusSimulador(escaparHtml(mensagemDeErroApi(criada, 'Não foi possível criar a tinta da mistura.')), 'erro');
                return;
            }
            tinta = criada.dados;
        }

        const resultado = await criarReceitaComIngredientes(nome, tinta.id, valor, proporcoes);
        if (!resultado.ok) {
            mostrarStatusSimulador(escaparHtml(resultado.mensagem), 'erro');
            carregarReceitas();
            return;
        }

        const origem = reaproveitada
            ? `Já existia a tinta <strong>${escaparHtml(tinta.nome)}</strong> com essa cor, então a receita foi ligada a ela.`
            : `A tinta <strong>${escaparHtml(tinta.nome)}</strong> (${escaparHtml(tinta.codigo)}) foi criada e já aparece no <a href="estoque.html">Estoque</a> com saldo 0.`;
        mostrarStatusSimulador(`Receita "${escaparHtml(nome)}" salva! ${origem} Use o botão de produzir na tabela abaixo para fabricar.`, 'sucesso');

        document.getElementById('simulador-nome').value = '';
        document.getElementById('simulador-valor').value = '';
        carregarReceitas();
    } catch (error) {
        console.error('Erro ao salvar a mistura:', error);
        mostrarStatusSimulador('Não foi possível salvar a mistura.', 'erro');
    } finally {
        botao.disabled = false;
    }
}

// Tela de Relatórios
// "Consumo" = saídas do estoque (venda, consumo na produção, perda, dano e ajuste de saída)
let movimentacoesRelatorio = [];
let coresRelatorio = new Map();

async function iniciarRelatorios() {
    document.getElementById('relatorio-periodo').addEventListener('change', gerarRelatorio);
    document.getElementById('relatorio-tipo').addEventListener('change', gerarRelatorio);

    const [resposta, estoques] = await Promise.all([
        buscarApi('/v1/movimentacao-estoque'),
        buscarLista('/v1/estoques')
    ]);

    if (!resposta.ok) {
        const texto = resposta.status === 401 || resposta.status === 403
            ? 'Você não tem permissão para ver os relatórios.'
            : 'Erro ao carregar as movimentações do banco de dados.';
        mensagemNaTabela(document.getElementById('tabela-relatorio-tintas-body'), texto, '#ef4444', 4);
        document.getElementById('rel-grafico').innerHTML = `<div class="grafico-vazio" style="color: #ef4444;">${texto}</div>`;
        return;
    }

    movimentacoesRelatorio = resposta.dados;
    coresRelatorio = new Map(estoques.map(e => [e.idEstoque, e.corHexadecimal]));
    gerarRelatorio();
}

function inicioDoDia(data) {
    return new Date(data.getFullYear(), data.getMonth(), data.getDate());
}

function formatarDiaMes(data) {
    return data.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' });
}

// Divide o período em faixas do gráfico: por dia (até 1 mês), por semana (trimestre) ou por mês (semestre/ano)
function faixasDoPeriodo(dias) {
    const hoje = inicioDoDia(new Date());
    const faixas = [];

    if (dias <= 31) {
        for (let i = dias - 1; i >= 0; i--) {
            const inicio = new Date(hoje.getFullYear(), hoje.getMonth(), hoje.getDate() - i);
            const fim = new Date(inicio.getFullYear(), inicio.getMonth(), inicio.getDate() + 1);
            faixas.push({ inicio, fim, rotulo: formatarDiaMes(inicio) });
        }
        return { faixas, agrupamento: 'Por dia' };
    }

    if (dias <= 120) {
        const semanas = Math.ceil(dias / 7);
        const amanha = new Date(hoje.getFullYear(), hoje.getMonth(), hoje.getDate() + 1);
        for (let i = semanas - 1; i >= 0; i--) {
            const fim = new Date(amanha.getFullYear(), amanha.getMonth(), amanha.getDate() - i * 7);
            const inicio = new Date(fim.getFullYear(), fim.getMonth(), fim.getDate() - 7);
            faixas.push({ inicio, fim, rotulo: formatarDiaMes(inicio) });
        }
        return { faixas, agrupamento: 'Por semana (data inicial)' };
    }

    const meses = Math.round(dias / 30);
    for (let i = meses - 1; i >= 0; i--) {
        const inicio = new Date(hoje.getFullYear(), hoje.getMonth() - i, 1);
        const fim = new Date(inicio.getFullYear(), inicio.getMonth() + 1, 1);
        const rotulo = `${NOMES_MESES[inicio.getMonth()]}/${String(inicio.getFullYear()).slice(2)}`;
        faixas.push({ inicio, fim, rotulo });
    }
    return { faixas, agrupamento: 'Por mês' };
}

function gerarRelatorio() {
    const dias = Number(document.getElementById('relatorio-periodo').value);
    const tipo = document.getElementById('relatorio-tipo').value;

    // Mostra só as seções do tipo escolhido
    document.getElementById('secao-consumo').hidden = tipo === 'tintas';
    document.getElementById('secao-tintas').hidden = tipo === 'consumo';

    const { faixas, agrupamento } = faixasDoPeriodo(dias);
    const inicioPeriodo = faixas[0].inicio;
    const fimPeriodo = faixas[faixas.length - 1].fim;

    const noPeriodo = movimentacoesRelatorio.filter(m => {
        const data = new Date(m.dataMovimentacao);
        return data >= inicioPeriodo && data < fimPeriodo;
    });
    const saidas = noPeriodo.filter(m => m.tipoMovimentacao === 'SAIDA');
    const entradas = noPeriodo.filter(m => m.tipoMovimentacao === 'ENTRADA');
    const consumoTotal = saidas.reduce((t, m) => t + (Number(m.quantidadeMovimentada) || 0), 0);

    const ultimoDia = new Date(fimPeriodo.getFullYear(), fimPeriodo.getMonth(), fimPeriodo.getDate() - 1);
    const textoPeriodo = `Período: ${inicioPeriodo.toLocaleDateString('pt-BR')} a ${ultimoDia.toLocaleDateString('pt-BR')}`;
    document.getElementById('relatorio-periodo-texto').textContent = textoPeriodo;

    // Cards
    const diasPeriodo = Math.max(1, Math.round((fimPeriodo - inicioPeriodo) / 86400000));
    const mediaMensal = Math.round(consumoTotal / (diasPeriodo / 30));
    document.getElementById('rel-consumo-total').textContent = consumoTotal;
    document.getElementById('rel-consumo-medio').textContent = mediaMensal;
    document.getElementById('rel-consumo-medio-sub').textContent = diasPeriodo < 30
        ? 'projeção para 30 dias'
        : 'média por mês no período';
    document.getElementById('rel-movimentacoes').textContent = noPeriodo.length;
    document.getElementById('rel-movimentacoes-sub').textContent =
        `${entradas.length} ${entradas.length === 1 ? 'entrada' : 'entradas'} · ${saidas.length} ${saidas.length === 1 ? 'saída' : 'saídas'}`;

    // Ranking de consumo por tinta (agrupado pelo estoque)
    const porEstoque = new Map();
    saidas.forEach(m => {
        const atual = porEstoque.get(m.estoqueId) || { nome: m.nomeTinta, codigo: m.codigoTinta, total: 0 };
        atual.total += Number(m.quantidadeMovimentada) || 0;
        porEstoque.set(m.estoqueId, atual);
    });
    const ranking = [...porEstoque.entries()]
        .map(([idEstoque, dados]) => ({ idEstoque, ...dados }))
        .sort((a, b) => b.total - a.total);

    if (ranking.length) {
        document.getElementById('rel-tinta-mais-usada').textContent = ranking[0].nome || 'Tinta removida';
        document.getElementById('rel-tinta-mais-usada-sub').textContent = `${ranking[0].total} un. consumidas`;
    } else {
        document.getElementById('rel-tinta-mais-usada').textContent = 'N/A';
        document.getElementById('rel-tinta-mais-usada-sub').textContent = 'sem consumo no período';
    }

    renderizarGraficoConsumo(faixas, saidas, agrupamento);
    renderizarRankingTintas(ranking, consumoTotal);
}

function renderizarGraficoConsumo(faixas, saidas, agrupamento) {
    const area = document.getElementById('rel-grafico');
    document.getElementById('rel-agrupamento').textContent = agrupamento;

    const valores = faixas.map(f => saidas
        .filter(m => {
            const data = new Date(m.dataMovimentacao);
            return data >= f.inicio && data < f.fim;
        })
        .reduce((t, m) => t + (Number(m.quantidadeMovimentada) || 0), 0));

    const maximo = Math.max(...valores);
    if (maximo === 0) {
        area.innerHTML = '<div class="grafico-vazio">Nenhum consumo registrado neste período.</div>';
        return;
    }

    const barras = faixas.map((f, i) => {
        const altura = Math.max(1, Math.round(valores[i] / maximo * 100));
        const titulo = `${f.rotulo}: ${valores[i]} un.`;
        return `
            <div class="barra-coluna" title="${escaparHtml(titulo)}">
                <span class="barra-valor">${valores[i] || ''}</span>
                <div class="barra ${valores[i] ? '' : 'vazia'}" style="height: ${valores[i] ? altura : 1}%;"></div>
            </div>`;
    }).join('');
    // Com muitas barras (ex.: 30 dias) mostra só uma data a cada N para não embolar; a última sempre aparece
    const passo = Math.ceil(faixas.length / 10);
    const rotulos = faixas.map((f, i) => {
        const mostrar = i % passo === 0 || i === faixas.length - 1;
        return `<span>${mostrar ? escaparHtml(f.rotulo) : ''}</span>`;
    }).join('');

    area.innerHTML = `<div class="grafico-barras">${barras}</div><div class="grafico-rotulos">${rotulos}</div>`;
}

function renderizarRankingTintas(ranking, consumoTotal) {
    const tbody = document.getElementById('tabela-relatorio-tintas-body');
    if (ranking.length === 0) {
        mensagemNaTabela(tbody, 'Nenhuma tinta consumida neste período.', '#64748b', 4);
        return;
    }

    tbody.innerHTML = '';
    ranking.forEach((item, i) => {
        const pct = consumoTotal ? Math.round(item.total / consumoTotal * 1000) / 10 : 0;
        const classePosicao = i < 3 ? `top${i + 1}` : '';
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><span class="posicao ${classePosicao}">${i + 1}º</span></td>
            <td>
                <div class="user-cell">
                    ${avatarTinta(coresRelatorio.get(item.idEstoque), item.nome)}
                    <div class="user-info-text">
                        <span class="name">${escaparHtml(item.nome || 'Tinta removida')}</span>
                        <span class="email">${escaparHtml(item.codigo || '-')}</span>
                    </div>
                </div>
            </td>
            <td><strong>${item.total}</strong> un.</td>
            <td>
                <div class="pct-cell">
                    <div class="pct-trilho"><div class="pct-preenchido" style="width: ${pct}%;"></div></div>
                    <span class="pct-texto">${pct.toLocaleString('pt-BR')}%</span>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Exportar PDF: abre a impressão do navegador (lá é só escolher "Salvar como PDF")
function exportarRelatorioPdf() {
    const tipo = document.getElementById('relatorio-tipo');
    document.getElementById('relatorio-gerado-em').textContent =
        `Relatório "${tipo.selectedOptions[0].text}" gerado em ${new Date().toLocaleString('pt-BR')} pelo VERF.`;
    window.print();
}

// Tela de Estoque: cadastro de nova tinta (o backend cria o estoque dela com saldo 0)
function origemTintaSelecionada() {
    const marcado = document.querySelector('input[name="tinta-origem"]:checked');
    return marcado ? marcado.value : 'COMPRADA';
}

// Fornecedor só vale para tinta comprada; o backend recusa fornecedor em tinta produzida
function atualizarCampoFornecedor() {
    document.getElementById('grupo-tinta-fornecedor').hidden = origemTintaSelecionada() !== 'COMPRADA';
}

async function abrirModalTinta() {
    const modal = document.getElementById('modal-tinta');
    if (!modal) return;

    document.getElementById('form-tinta').reset();
    document.getElementById('tinta-cor-hex').value = '#154EC1';
    mostrarErroModal('modal-tinta-erro', '');
    atualizarCampoFornecedor();

    const select = document.getElementById('tinta-fornecedor');
    select.innerHTML = '<option value="">Carregando fornecedores...</option>';
    modal.hidden = false;
    document.getElementById('tinta-nome').focus();

    const fornecedores = (await buscarLista('/v1/fornecedores'))
        .filter(f => f.ativo)
        .sort((a, b) => String(a.nome).localeCompare(String(b.nome)));
    select.innerHTML = fornecedores.length
        ? '<option value="">Selecione o fornecedor</option>' + fornecedores
            .map(f => `<option value="${Number(f.idFornecedor)}">${escaparHtml(f.nome)}</option>`).join('')
        : '<option value="">Nenhum fornecedor ativo cadastrado</option>';
}

function fecharModalTinta() {
    const modal = document.getElementById('modal-tinta');
    if (modal) modal.hidden = true;
}

function ativarFormularioTinta() {
    const form = document.getElementById('form-tinta');
    if (!form) return;

    ativarFechamentoModal('modal-tinta', fecharModalTinta);
    document.querySelectorAll('input[name="tinta-origem"]').forEach(r => r.addEventListener('change', atualizarCampoFornecedor));

    // Quadrado de cor e campo hex andam juntos
    const seletor = document.getElementById('tinta-cor');
    const campoHex = document.getElementById('tinta-cor-hex');
    seletor.addEventListener('input', () => { campoHex.value = seletor.value.toUpperCase(); });
    campoHex.addEventListener('input', () => {
        let valor = campoHex.value.trim();
        if (valor && !valor.startsWith('#')) valor = '#' + valor;
        if (corValida(valor)) seletor.value = valor.toLowerCase();
    });

    form.addEventListener('submit', salvarTinta);
}

async function salvarTinta(event) {
    event.preventDefault();

    const nome = document.getElementById('tinta-nome').value.trim();
    const codigo = document.getElementById('tinta-codigo').value.trim().toUpperCase();
    let hex = document.getElementById('tinta-cor-hex').value.trim().toUpperCase();
    if (hex && !hex.startsWith('#')) hex = '#' + hex;
    const origem = origemTintaSelecionada();
    const idFornecedor = document.getElementById('tinta-fornecedor').value;

    if (!nome || !codigo) {
        mostrarErroModal('modal-tinta-erro', 'Preencha o nome e o código da tinta.');
        return;
    }
    if (codigo.length < 7 || codigo.length > 20) {
        mostrarErroModal('modal-tinta-erro', 'O código precisa ter entre 7 e 20 caracteres.');
        return;
    }
    if (!corValida(hex)) {
        mostrarErroModal('modal-tinta-erro', 'A cor precisa estar no formato #RRGGBB (ex.: #154EC1).');
        return;
    }
    if (origem === 'COMPRADA' && !idFornecedor) {
        mostrarErroModal('modal-tinta-erro', 'Tinta comprada precisa de um fornecedor.');
        return;
    }

    const dados = { nome, codigo, numeroHexadecimal: hex, origem };
    if (origem === 'COMPRADA') dados.idFornecedor = Number(idFornecedor);

    const botao = document.getElementById('btn-salvar-tinta');
    botao.disabled = true;
    mostrarErroModal('modal-tinta-erro', '');

    try {
        const resultado = await enviarApi('/v1/tintas', dados);
        if (!resultado.ok) {
            // 409 = código ou cor já cadastrados; a API explica no campo "mensagem"
            mostrarErroModal('modal-tinta-erro', mensagemDeErroApi(resultado, 'Não foi possível cadastrar a tinta.'));
            return;
        }
        fecharModalTinta();
        carregarEstoque();
    } catch (error) {
        console.error('Erro ao cadastrar tinta:', error);
        mostrarErroModal('modal-tinta-erro', 'Não foi possível cadastrar a tinta.');
    } finally {
        botao.disabled = false;
    }
}

// Dashboard: baixa o estoque atual como planilha CSV (abre no Excel)
function campoCsv(valor) {
    const texto = String(valor ?? '');
    return /[;"\n\r]/.test(texto) ? `"${texto.replace(/"/g, '""')}"` : texto;
}

async function exportarEstoqueCsv() {
    const botao = document.getElementById('btn-exportar-estoque');
    if (botao) botao.disabled = true;

    try {
        const resposta = await buscarApi('/v1/estoques');
        if (!resposta.ok) {
            alert(resposta.status === 401 || resposta.status === 403
                ? 'Você não tem permissão para exportar o estoque.'
                : 'Não foi possível carregar o estoque para exportar.');
            return;
        }
        if (resposta.dados.length === 0) {
            alert('O estoque está vazio, não há nada para exportar.');
            return;
        }

        const cabecalho = ['Tinta', 'Código', 'Cor', 'Origem', 'Quantidade', 'Situação', 'Atualizado em'];
        const linhas = [...resposta.dados]
            .sort((a, b) => String(a.nomeTinta).localeCompare(String(b.nomeTinta)))
            .map(item => [
                item.nomeTinta,
                item.codigoTinta,
                item.corHexadecimal,
                item.origemTinta === 'PRODUZIDA' ? 'Produzida' : item.origemTinta === 'COMPRADA' ? 'Comprada' : '',
                item.quantidade,
                situacaoEstoque(Number(item.quantidade)).texto,
                formatarDataHora(item.atualizadoEm)
            ]);

        // Ponto e vírgula e BOM UTF-8: o Excel em português abre com colunas e acentos certos
        const csv = '﻿' + [cabecalho, ...linhas].map(l => l.map(campoCsv).join(';')).join('\r\n');
        const link = document.createElement('a');
        link.href = URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8' }));
        link.download = `estoque-verf-${hojeISO()}.csv`;
        document.body.appendChild(link);
        link.click();
        link.remove();
        setTimeout(() => URL.revokeObjectURL(link.href), 1000);
    } catch (error) {
        console.error('Erro ao exportar estoque:', error);
        alert('Não foi possível exportar o estoque.');
    } finally {
        if (botao) botao.disabled = false;
    }
}
