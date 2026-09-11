document.addEventListener('DOMContentLoaded', () => {
    const recoveryForm = document.getElementById('recovery-form');
    const emailInput = document.getElementById('recovery-email');
    const feedbackMessage = document.getElementById('feedback-message');
    const btnSubmit = document.getElementById('btn-recovery');

    recoveryForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const email = emailInput.value.trim();

        if (!email) {
            showFeedback('Por favor, digite um e-mail válido.', 'error');
            return;
        }

        // Altera o estado do botão para carregando
        btnSubmit.disabled = true;
        btnSubmit.innerHTML = '<span>Enviando...</span>';

        try {
            /*  Comentario -- Renan
                Deixando essa estrutura pronta para fazer teste quando o Backend no Spring Boot estiver pronto, pode descomentar essas linhas abaio para poder fazer o esqueci a senha funcional:
               
               const response = await fetch('/api/auth/esqueci-senha', {
                   method: 'POST',
                   headers: { 'Content-Type': 'application/json' },
                   body: JSON.stringify({ email })
               });

               if (!response.ok) throw new Error('Erro ao solicitar redefinição.');
            */

            //tempo de resposta da API (1.5 segundos)
            await new Promise(resolve => setTimeout(resolve, 1500));

            //Exibe mensagem de sucesso
            showFeedback('Link de recuperação enviado para o seu e-mail! Verifique sua caixa de entrada.', 'success');
            recoveryForm.reset();

        } catch (error) {
            showFeedback('Não foi possível enviar o e-mail. Tente novamente mais tarde.', 'error');
        } finally {
            // Restaura o botão
            btnSubmit.disabled = false;
            btnSubmit.innerHTML = '<span>Enviar Link de Recuperação</span>';
        }
    });

    function showFeedback(message, type) {
        feedbackMessage.textContent = message;
        feedbackMessage.className = `feedback-message ${type}`;
        feedbackMessage.classList.remove('hidden');
    }
});