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
    }

    // Só carrega os indicadores se a página for o dashboard
    if (document.getElementById('card-total-tintas')) {
        carregarDashboard();
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

function mensagemNaTabela(tbody, texto, cor) {
    tbody.innerHTML = `
        <tr>
            <td colspan="6" style="text-align: center; color: ${cor}; padding: 24px;">
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

    itemMovimentacao = item;
    document.getElementById('form-movimentacao').reset();
    mostrarErroMovimentacao('');

    const cor = corValida(item.corHexadecimal);
    const amostra = document.getElementById('mov-cor');
    amostra.style.backgroundColor = cor || '#154ec1';
    document.getElementById('mov-tinta-nome').textContent = item.nomeTinta || 'Tinta sem nome';
    document.getElementById('mov-tinta-codigo').textContent = item.codigoTinta || 'Sem código';
    document.getElementById('mov-saldo-atual').textContent = item.quantidade;

    preencherMotivos();
    atualizarPreviaSaldo();

    modal.hidden = false;
    document.getElementById('mov-quantidade').focus();
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
        carregarEstoque();
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
