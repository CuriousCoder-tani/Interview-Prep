function anime() {
    const one = document.getElementById("one");
    const two = document.getElementById("two");
    const three = document.getElementById("three");

    [one, two, three].forEach(el => {
        el.style.animation = "fadeIn 1s forwards";
    });
}

// Countdown Timer
let time = 105;
function startCountdown() {
    const timerElement = document.getElementById("timer");
    const interval = setInterval(() => {
        const minutes = Math.floor(time / 60);
        const seconds = time % 60;
        timerElement.textContent = `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
        if (time <= 0) {
            clearInterval(interval);
            timerElement.textContent = "Time's up!";
            alert("Time's up!");
            const submitBtn = document.getElementById("submitBtn");
            if (submitBtn) {
                submitBtn.click();
            }
            return;
        }
        time--;
    }, 1000);
}

// Speech Recognition
function setupSpeechRecognition() {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) {
        alert('Speech Recognition API is not supported in this browser.');
        return;
    }

    const recognition = new SpeechRecognition();
    recognition.interimResults = true;
    recognition.lang = 'en-US';

    let isRecording = false;
    const voiceIndicator = document.getElementById('voice-indicator');
    const textArea = document.querySelector('.text-response');

    voiceIndicator?.addEventListener('click', function () {
        isRecording = !isRecording;

        if (isRecording) {
            this.classList.add('recording');
            this.innerHTML = '<i class="fas fa-stop"></i>';
            recognition.start();
        } else {
            this.classList.remove('recording');
            this.innerHTML = '<i class="fas fa-microphone"></i>';
            recognition.stop();
        }
    });

    recognition.onresult = function (event) {
        let transcript = '';
        for (let i = event.resultIndex; i < event.results.length; i++) {
            transcript += event.results[i][0].transcript;
        }
        textArea.value = transcript;
    };

    recognition.onend = function () {
        if (!isRecording) {
            document.querySelector('.btn.btn-primary')?.click();
        }
    };
}

// Tab Toggle
function setupTabToggle() {
    document.querySelectorAll('.response-tab').forEach(tab => {
        tab.addEventListener('click', function () {
            document.querySelectorAll('.response-tab').forEach(t => t.classList.remove('active'));
            this.classList.add('active');

            const tabType = this.getAttribute('data-tab');
            document.getElementById('text-response').classList.add('d-none');
            document.getElementById('voice-response').classList.add('d-none');
            document.getElementById(`${tabType}-response`).classList.remove('d-none');
        });
    });
}

// Voice Timer
function setupVoiceTimer() {
    const voiceIndicator = document.getElementById('voice-indicator');
    let isRecording = false;
    let timerInterval;
    let seconds = 0;

    voiceIndicator?.addEventListener('click', function () {
        isRecording = !isRecording;
        if (isRecording) {
            this.classList.add('recording');
            this.innerHTML = '<i class="fas fa-stop"></i>';
            seconds = 0;
            timerInterval = setInterval(() => {
                seconds++;
                const mins = Math.floor(seconds / 60).toString().padStart(2, '0');
                const secs = (seconds % 60).toString().padStart(2, '0');
                document.querySelector('.voice-timer').textContent = `${mins}:${secs}`;
            }, 1000);
        } else {
            this.classList.remove('recording');
            this.innerHTML = '<i class="fas fa-microphone"></i>';
            clearInterval(timerInterval);
        }
    });
}

// Form Validation
function setupFormValidation() {
    const submitBtn = document.getElementById("submitBtn");
    if (submitBtn) {
        submitBtn.addEventListener("click", function (e) {
            const answer = document.querySelector(".text-response")?.value.trim();
            if (!answer) {
                e.preventDefault();
                alert("Please type an answer before submitting.");
            }
        });
    }
}

// Clear Button Functionality
function setupClearButton() {
    document.querySelectorAll('.btn-outline').forEach(button => {
        button.addEventListener('click', function () {
            const activeTab = document.querySelector('.response-tab.active').getAttribute('data-tab');

            if (activeTab === 'text') {
                const textArea = document.querySelector('.text-response');
                textArea.value = '';
            } else if (activeTab === 'voice') {
                document.querySelector('.voice-timer').textContent = '00:00';
                const voiceIndicator = document.getElementById('voice-indicator');
                voiceIndicator.classList.remove('recording');
                voiceIndicator.innerHTML = '<i class="fas fa-microphone"></i>';
            }
        });
    });
}

// Initialize everything on page load
window.onload = function () {
    startCountdown();
    setupSpeechRecognition();
    setupTabToggle();
    setupVoiceTimer();
    setupFormValidation();
    setupClearButton();
};
