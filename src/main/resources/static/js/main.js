// MindMate AI Client Suite (Mood, Breathing, Grounding, Timer, Goals, Chat)
document.addEventListener('DOMContentLoaded', () => {

    // =========================================================================
    // 1. Mobile Menu Toggle
    // =========================================================================
    const menuBtn = document.getElementById('menuBtn');
    const nav = document.getElementById('nav');
    if (menuBtn && nav) {
        menuBtn.addEventListener('click', () => nav.classList.toggle('open'));
        nav.querySelectorAll('a').forEach(link => {
            link.addEventListener('click', () => nav.classList.remove('open'));
        });
    }

    // =========================================================================
    // 2. Interactive Range Sliders
    // =========================================================================
    const sliders = [
        { id: 'stress', out: 'stressOut', fmt: v => `${v} / 10` },
        { id: 'sleep', out: 'sleepOut', fmt: v => `${v} hours` },
        { id: 'workload', out: 'workloadOut', fmt: v => `${v} / 10` },
        { id: 'energy', out: 'energyOut', fmt: v => `${v} / 10` },
        { id: 'connection', out: 'connectionOut', fmt: v => `${v} / 10` }
    ];

    sliders.forEach(({ id, out, fmt }) => {
        const input = document.getElementById(id);
        const output = document.getElementById(out);
        if (input && output) {
            output.textContent = fmt(input.value);
            input.addEventListener('input', () => {
                output.textContent = fmt(input.value);
            });
        }
    });

    // =========================================================================
    // 3. Emoji Mood Tracker Selector
    // =========================================================================
    const moodChips = document.querySelectorAll('.mood-chip');
    const moodScoreInput = document.getElementById('moodScore');
    const moodEmojiInput = document.getElementById('moodEmoji');
    const moodLabelInput = document.getElementById('moodLabel');
    const selectedMoodDisplay = document.getElementById('selectedMoodDisplay');

    moodChips.forEach(chip => {
        chip.addEventListener('click', () => {
            moodChips.forEach(c => c.classList.remove('selected'));
            chip.classList.add('selected');

            const score = chip.getAttribute('data-score');
            const emoji = chip.getAttribute('data-emoji');
            const label = chip.getAttribute('data-label');

            if (moodScoreInput) moodScoreInput.value = score;
            if (moodEmojiInput) moodEmojiInput.value = emoji;
            if (moodLabelInput) moodLabelInput.value = label;

            if (selectedMoodDisplay) {
                selectedMoodDisplay.textContent = `${emoji} ${label} (${score}/5)`;
            }
        });
    });

    // =========================================================================
    // 4. Guided Breathing Pacer (Box Breathing & 4-7-8)
    // =========================================================================
    const breathingCircle = document.getElementById('breathingCircle');
    const breathingAction = document.getElementById('breathingAction');
    const breathingTimer = document.getElementById('breathingTimer');
    const cycleCountDisplay = document.getElementById('cycleCountDisplay');
    const startBreatheBtn = document.getElementById('startBreatheBtn');
    const pauseBreatheBtn = document.getElementById('pauseBreatheBtn');
    const resetBreatheBtn = document.getElementById('resetBreatheBtn');
    const techniqueBoxBtn = document.getElementById('techniqueBoxBtn');
    const techniqueRelaxBtn = document.getElementById('techniqueRelaxBtn');

    if (breathingCircle && startBreatheBtn) {
        const boxPhases = [
            { text: 'Inhale slowly...', duration: 4, cls: 'inhale' },
            { text: 'Hold breath...', duration: 4, cls: 'hold' },
            { text: 'Exhale gently...', duration: 4, cls: 'exhale' },
            { text: 'Hold empty...', duration: 4, cls: 'hold-out' }
        ];

        const relaxPhases = [
            { text: 'Inhale through nose...', duration: 4, cls: 'inhale' },
            { text: 'Hold breath steady...', duration: 7, cls: 'hold' },
            { text: 'Exhale through mouth...', duration: 8, cls: 'exhale' }
        ];

        let currentPhases = boxPhases;
        let isBreathingRunning = false;
        let isBreathingPaused = false;
        let currentPhaseIdx = 0;
        let secondsLeftInPhase = 0;
        let cyclesCompleted = 0;
        let breathingInterval = null;

        function setTechnique(phases, activeBtn, otherBtn) {
            resetBreathing();
            currentPhases = phases;
            activeBtn.classList.add('active');
            otherBtn.classList.remove('active');
        }

        if (techniqueBoxBtn && techniqueRelaxBtn) {
            techniqueBoxBtn.addEventListener('click', () => setTechnique(boxPhases, techniqueBoxBtn, techniqueRelaxBtn));
            techniqueRelaxBtn.addEventListener('click', () => setTechnique(relaxPhases, techniqueRelaxBtn, techniqueBoxBtn));
        }

        function updateCircleDisplay(phase, seconds) {
            breathingCircle.className = `breathing-circle ${phase.cls}`;
            breathingCircle.style.transitionDuration = `${phase.duration}s`;
            breathingAction.textContent = phase.text;
            breathingTimer.textContent = `${seconds}s`;
        }

        function startBreathing() {
            if (isBreathingRunning && isBreathingPaused) {
                // Resume
                isBreathingPaused = false;
                startBreatheBtn.disabled = true;
                pauseBreatheBtn.disabled = false;
                pauseBreatheBtn.textContent = '⏸ Pause';
                tick();
                breathingInterval = setInterval(tick, 1000);
                return;
            }

            isBreathingRunning = true;
            isBreathingPaused = false;
            startBreatheBtn.disabled = true;
            pauseBreatheBtn.disabled = false;
            pauseBreatheBtn.textContent = '⏸ Pause';

            currentPhaseIdx = 0;
            secondsLeftInPhase = currentPhases[0].duration;
            updateCircleDisplay(currentPhases[0], secondsLeftInPhase);

            breathingInterval = setInterval(tick, 1000);
        }

        function tick() {
            secondsLeftInPhase--;
            if (secondsLeftInPhase <= 0) {
                currentPhaseIdx = (currentPhaseIdx + 1) % currentPhases.length;
                if (currentPhaseIdx === 0) {
                    cyclesCompleted++;
                    if (cycleCountDisplay) {
                        cycleCountDisplay.innerHTML = `Cycles completed: <strong>${cyclesCompleted}</strong>`;
                    }
                }
                const phase = currentPhases[currentPhaseIdx];
                secondsLeftInPhase = phase.duration;
                updateCircleDisplay(phase, secondsLeftInPhase);
            } else {
                breathingTimer.textContent = `${secondsLeftInPhase}s`;
            }
        }

        function pauseBreathing() {
            if (!isBreathingRunning) return;
            if (!isBreathingPaused) {
                clearInterval(breathingInterval);
                isBreathingPaused = true;
                pauseBreatheBtn.textContent = '▶ Resume';
                breathingAction.textContent = 'Paused';
                startBreatheBtn.disabled = false;
            } else {
                startBreathing();
            }
        }

        function resetBreathing() {
            clearInterval(breathingInterval);
            isBreathingRunning = false;
            isBreathingPaused = false;
            currentPhaseIdx = 0;
            secondsLeftInPhase = 0;
            breathingCircle.className = 'breathing-circle';
            breathingCircle.style.transitionDuration = '0.5s';
            breathingAction.textContent = 'Ready';
            breathingTimer.textContent = 'Press Start';
            startBreatheBtn.disabled = false;
            pauseBreatheBtn.disabled = true;
            pauseBreatheBtn.textContent = '⏸ Pause';
        }

        startBreatheBtn.addEventListener('click', startBreathing);
        pauseBreatheBtn.addEventListener('click', pauseBreathing);
        resetBreatheBtn.addEventListener('click', resetBreathing);
    }

    // =========================================================================
    // 5. 5-4-3-2-1 Sensory Grounding Tool
    // =========================================================================
    const groundingChecks = document.querySelectorAll('.grounding-check');
    const groundingFeedback = document.getElementById('groundingFeedback');
    const resetGroundingBtn = document.getElementById('resetGroundingBtn');

    if (groundingChecks.length > 0) {
        function checkGroundingProgress() {
            const allChecked = Array.from(groundingChecks).every(c => c.checked);
            if (groundingFeedback) {
                groundingFeedback.style.display = allChecked ? 'block' : 'none';
            }
        }

        groundingChecks.forEach(ch => {
            ch.addEventListener('change', checkGroundingProgress);
        });

        if (resetGroundingBtn) {
            resetGroundingBtn.addEventListener('click', () => {
                groundingChecks.forEach(ch => ch.checked = false);
                if (groundingFeedback) groundingFeedback.style.display = 'none';
            });
        }
    }

    // =========================================================================
    // 6. Study-Break & Focus Timer (with Web Audio chime)
    // =========================================================================
    const timerCountdown = document.getElementById('timerCountdown');
    const timerStatusLabel = document.getElementById('timerStatusLabel');
    const timerTip = document.getElementById('timerTip');
    const startTimerBtn = document.getElementById('startTimerBtn');
    const pauseTimerBtn = document.getElementById('pauseTimerBtn');
    const resetTimerBtn = document.getElementById('resetTimerBtn');
    const timerPresetBtns = document.querySelectorAll('.timer-preset-btn');

    if (timerCountdown && startTimerBtn) {
        let totalTimerSeconds = 5 * 60;
        let timerSecondsRemaining = totalTimerSeconds;
        let timerInterval = null;
        let isTimerRunning = false;
        let isTimerPaused = false;

        function playChime() {
            try {
                const ctx = new (window.AudioContext || window.webkitAudioContext)();
                const osc = ctx.createOscillator();
                const gain = ctx.createGain();
                osc.type = 'sine';
                osc.frequency.setValueAtTime(587.33, ctx.currentTime); // D5
                osc.frequency.exponentialRampToValueAtTime(880, ctx.currentTime + 0.3); // A5
                gain.gain.setValueAtTime(0.15, ctx.currentTime);
                gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 1.2);
                osc.connect(gain);
                gain.connect(ctx.destination);
                osc.start();
                osc.stop(ctx.currentTime + 1.2);
            } catch (e) {
                // AudioContext not allowed or not supported; graceful fallback
            }
        }

        function formatTime(secs) {
            const m = Math.floor(secs / 60).toString().padStart(2, '0');
            const s = (secs % 60).toString().padStart(2, '0');
            return `${m}:${s}`;
        }

        function updateTimerDisplay() {
            timerCountdown.textContent = formatTime(timerSecondsRemaining);
        }

        timerPresetBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                timerPresetBtns.forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
                const mins = parseInt(btn.getAttribute('data-minutes'), 10) || 5;
                const type = btn.getAttribute('data-type');

                resetTimer();
                totalTimerSeconds = mins * 60;
                timerSecondsRemaining = totalTimerSeconds;
                updateTimerDisplay();

                if (type === 'focus') {
                    if (timerStatusLabel) timerStatusLabel.textContent = 'Focus Sprint';
                    if (timerTip) timerTip.textContent = 'Commit to working on a single task without tab switching.';
                } else {
                    if (timerStatusLabel) timerStatusLabel.textContent = 'Rest Interval';
                    if (timerTip) timerTip.textContent = 'Step away from your chair, look at distant greenery, or stretch.';
                }
            });
        });

        function startTimer() {
            if (isTimerRunning && isTimerPaused) {
                isTimerPaused = false;
                startTimerBtn.disabled = true;
                pauseTimerBtn.disabled = false;
                timerInterval = setInterval(timerTick, 1000);
                return;
            }

            isTimerRunning = true;
            isTimerPaused = false;
            startTimerBtn.disabled = true;
            pauseTimerBtn.disabled = false;
            timerInterval = setInterval(timerTick, 1000);
        }

        function timerTick() {
            timerSecondsRemaining--;
            updateTimerDisplay();

            if (timerSecondsRemaining <= 0) {
                clearInterval(timerInterval);
                isTimerRunning = false;
                startTimerBtn.disabled = false;
                pauseTimerBtn.disabled = true;
                playChime();
                if (timerStatusLabel) timerStatusLabel.textContent = 'Time Reached ✨';
                if (timerTip) timerTip.textContent = 'Interval complete. Take a gentle breath and acknowledge your pacing.';
            }
        }

        function pauseTimer() {
            if (!isTimerRunning) return;
            if (!isTimerPaused) {
                clearInterval(timerInterval);
                isTimerPaused = true;
                pauseTimerBtn.textContent = '▶ Resume';
                startTimerBtn.disabled = false;
            } else {
                startTimer();
                pauseTimerBtn.textContent = '⏸ Pause';
            }
        }

        function resetTimer() {
            clearInterval(timerInterval);
            isTimerRunning = false;
            isTimerPaused = false;
            timerSecondsRemaining = totalTimerSeconds;
            updateTimerDisplay();
            startTimerBtn.disabled = false;
            pauseTimerBtn.disabled = true;
            pauseTimerBtn.textContent = '⏸ Pause';
        }

        startTimerBtn.addEventListener('click', startTimer);
        pauseTimerBtn.addEventListener('click', pauseTimer);
        resetTimerBtn.addEventListener('click', resetTimer);
    }

    // =========================================================================
    // 7. Goals Checklist Toggle (Interactive AJAX with UI feedback)
    // =========================================================================
    const goalCheckboxes = document.querySelectorAll('.goal-checkbox');
    const goalProgressBar = document.getElementById('goalProgressBar');

    goalCheckboxes.forEach(cb => {
        cb.addEventListener('change', async () => {
            const goalId = cb.getAttribute('data-id');
            const item = cb.closest('.goal-item');
            const titleSpan = item ? item.querySelector('.goal-title-text') : null;

            // Optimistic update
            const isCompleted = cb.checked;
            if (item) item.classList.toggle('completed', isCompleted);
            if (titleSpan) {
                titleSpan.style.textDecoration = isCompleted ? 'line-through' : 'none';
                titleSpan.style.color = isCompleted ? '#88968e' : 'var(--ink)';
            }

            try {
                const res = await fetch(`/goals/${goalId}/toggle`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' }
                });

                if (res.ok) {
                    // Update progress bar dynamically
                    const allBoxes = document.querySelectorAll('.goal-checkbox');
                    const completedCount = Array.from(allBoxes).filter(b => b.checked).length;
                    const percent = Math.round((completedCount / allBoxes.length) * 100);
                    if (goalProgressBar) {
                        goalProgressBar.style.width = `${percent}%`;
                    }
                }
            } catch (e) {
                console.warn('Goal toggle AJAX failed, fallback handled on reload');
            }
        });
    });

    // Preset Goal Chips auto-fill
    const presetGoalChips = document.querySelectorAll('.preset-goal-chip');
    const goalTitleInput = document.getElementById('goalTitle');
    const goalCategorySelect = document.getElementById('goalCategory');

    presetGoalChips.forEach(chip => {
        chip.addEventListener('click', () => {
            const title = chip.getAttribute('data-title');
            const cat = chip.getAttribute('data-cat');
            if (goalTitleInput && title) {
                goalTitleInput.value = title;
                goalTitleInput.focus();
            }
            if (goalCategorySelect && cat) {
                goalCategorySelect.value = cat;
            }
        });
    });

    // =========================================================================
    // 8. Companion AI Chat (Interactive AJAX with typing indicator & safety)
    // =========================================================================
    const chatForm = document.getElementById('chatForm');
    const chatInput = document.getElementById('chatInput');
    const messages = document.getElementById('messages');
    const convIdInput = document.getElementById('convId');

    function appendMessage(text, role, isCrisis = false) {
        if (!messages) return;
        const p = document.createElement('p');
        p.className = role === 'user' ? 'user' : 'bot';
        if (isCrisis) {
            p.classList.add('crisis');
            p.style.border = '2px solid #b8382c';
            p.style.background = '#fff4f4';
            p.style.color = '#7a1f17';
            p.style.fontWeight = '500';
        }
        p.textContent = text;
        messages.appendChild(p);
        messages.scrollTop = messages.scrollHeight;
    }

    async function sendCompanionMessage(text) {
        const clean = text.trim();
        if (!clean) return;

        appendMessage(clean, 'user');
        if (chatInput) chatInput.value = '';

        // Typing indicator
        const typingP = document.createElement('p');
        typingP.className = 'bot typing';
        typingP.innerHTML = '<em>MindMate is reflecting... 🌱</em>';
        if (messages) {
            messages.appendChild(typingP);
            messages.scrollTop = messages.scrollHeight;
        }

        const convId = convIdInput ? convIdInput.value : '';

        try {
            const res = await fetch('/companion/message', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ message: clean, conversationId: convId })
            });

            if (typingP.parentNode) typingP.parentNode.removeChild(typingP);

            if (res.ok) {
                const data = await res.json();
                appendMessage(data.reply, 'bot', data.crisisAlert);
            } else {
                appendMessage("I hear you. 🌱 Take a gentle pause and drink some water. What is one small thing on your mind right now?", 'bot');
            }
        } catch (e) {
            if (typingP.parentNode) typingP.parentNode.removeChild(typingP);
            appendMessage("I hear you. 🌱 Take a gentle pause and drink some water.", 'bot');
        }
    }

    if (chatForm && chatInput) {
        chatForm.addEventListener('submit', (e) => {
            e.preventDefault();
            sendCompanionMessage(chatInput.value);
        });
    }

    // Prompt Chips
    document.querySelectorAll('[data-prompt]').forEach(btn => {
        btn.addEventListener('click', () => {
            const prompt = btn.getAttribute('data-prompt');
            sendCompanionMessage(prompt);
            const companionSec = document.getElementById('companion');
            if (companionSec) {
                companionSec.scrollIntoView({ behavior: 'smooth' });
            }
        });
    });

    // Auto-scroll chat to bottom on load
    if (messages) {
        messages.scrollTop = messages.scrollHeight;
    }
});
