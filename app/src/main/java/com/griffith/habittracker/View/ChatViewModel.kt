// BSCH - Stage 4
// Software Development Project [BSCH-SDP/Dub/FT]
// Project Name - BetterThanYesterday

package com.griffith.habittracker.View

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.griffith.habittracker.BuildConfig
import com.griffith.habittracker.Controller.StreakController
import com.griffith.habittracker.Model.UserAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatItem(val content: String, val isFromUser: Boolean)

class ChatViewModel : ViewModel() {
    private val apiKey = BuildConfig.GEMINI_API_KEY

    private val _messages = MutableStateFlow(listOf<ChatItem>())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Store conversation history for memory
    private val conversationHistory = mutableListOf<String>()

    fun initializeChat(context: Context) {
        if (_messages.value.isEmpty()) {
            val username = getUsername(context)
            val habit = getHabit(context)

            val welcome = "Hi $username! 👋 I'm your personal habit-breaking coach. I'm here to help you quit $habit with science-backed strategies and motivation. How are you feeling today?"
            _messages.value = listOf(ChatItem(welcome, false))
            conversationHistory.add("Assistant: $welcome")
        }
    }

    fun sendMessage(userMessage: String, context: Context) {
        if (userMessage.trim().isEmpty()) return

        _messages.value += ChatItem(userMessage, true)
        _isLoading.value = true
        conversationHistory.add("User: $userMessage")

        viewModelScope.launch {
            try {
                // Step 1: Check simple commands first
                val quickResponse = checkSimpleCommands(userMessage, context)
                if (quickResponse != null) {
                    _messages.value += ChatItem(quickResponse, false)
                    conversationHistory.add("Assistant: $quickResponse")
                    _isLoading.value = false
                    return@launch
                }

                // Step 2: Use Gemini for complex responses
                val model = GenerativeModel("gemini-1.5-pro", apiKey)
                val prompt = buildEnhancedPrompt(userMessage, context)
                val response = model.generateContent(prompt)

                val responseText = response.text ?: "Sorry, I couldn't generate a response."
                _messages.value += ChatItem(responseText, false)
                conversationHistory.add("Assistant: $responseText")

            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error: ${e.message}")
                _messages.value += ChatItem("Sorry, I had an error. Please try again.", false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun buildEnhancedPrompt(userMessage: String, context: Context): String {
        val username = getUsername(context)
        val habit = getHabit(context)
        val reason = getReason(context)
        val days = StreakController.days.value

        return """
You are an expert habit-breaking coach and motivational assistant helping $username quit $habit.

USER CONTEXT:
- Name: $username
- Quitting: $habit
- Their reason: $reason
- Current streak: $days days

INSTRUCTIONS:
- Use their name and be supportive
- Include educational facts about neuroscience, habit formation, and recovery
- Remind them that urges typically last 15-20 minutes and WILL pass
- Explain the science behind why quitting is hard but gets easier
- Share motivational insights about brain changes and willpower
- Reference their progress and specific situation
- Keep responses helpful but not too long

CONVERSATION HISTORY:
${conversationHistory.takeLast(6).joinToString("\n")}

User: $userMessage

Respond with educational motivation:"""
    }

    private fun checkSimpleCommands(message: String, context: Context): String? {
        val msg = message.lowercase().trim()
        val username = getUsername(context)
        val habit = getHabit(context)
        val reason = getReason(context)

        return when {
            msg.contains("my stats") || msg.contains("progress") || msg.contains("how am i doing") -> {
                val days = StreakController.days.value
                val hours = StreakController.hours.value
                val relapses = UserAnalytics.getRelapseCount(context)
                val longestDays = UserAnalytics.getLongestStreakDays(context)

                "📊 Hey $username! Here's your progress:\n\n🔥 Current streak: $days days, $hours hours\n🏆 Best streak: $longestDays days\n📉 Total relapses: $relapses\n\n💡 **Did you know?** Every day you resist $habit, your brain creates new neural pathways that make it easier to resist next time. You're literally rewiring your brain for success! 🧠✨\n\nKeep it up! 💪"
            }

            msg.contains("help") || msg.contains("emergency") || msg.contains("urge") || msg.contains("craving") -> {
                "🚨 $username, you got this! \n\n⏰ **IMPORTANT**: Most urges only last 15-20 minutes! This feeling WILL pass.\n\n🛡️ **Immediate Actions:**\n💨 Take 5 deep breaths (activates your calm response)\n🚿 Splash cold water on face (resets your nervous system)\n🏃 Do 10 jumping jacks (releases endorphins)\n🚪 Step outside (changes environment = changes mindset)\n\n🧠 **Science Fact**: Your brain is trying to trick you with fake urgency. The craving you feel is just old neural pathways firing - they get weaker every time you don't give in!\n\nRemember: $reason. This urge will pass! 💪"
            }

            msg.contains("motivation") || msg.contains("motivate me") || msg.contains("encourage") -> {
                val days = StreakController.days.value
                getEducationalMotivation(username, habit, reason, days)
            }

            msg.contains("my streak") || msg.contains("current streak") -> {
                val days = StreakController.days.value
                val hours = StreakController.hours.value
                val minutes = StreakController.minutes.value

                when {
                    days > 0 -> "🔥 $username, you've been clean for $days days and $hours hours!\n\n📚 **Recovery Timeline**: At $days days, your body has likely cleared most toxins and your brain chemistry is starting to rebalance. Amazing progress! 🎉"
                    hours > 0 -> "🔥 $username, you've been clean for $hours hours and $minutes minutes!\n\n🔬 **Science**: Even in the first 24 hours, your body starts healing and your willpower gets stronger with each hour. Every hour counts! ⚡"
                    else -> "🔥 $username, you've been clean for $minutes minutes!\n\n🌱 **The hardest part**: The first few hours are the toughest because your brain is used to the old pattern. You're breaking the cycle right now - that's incredible courage! 💪"
                }
            }

            msg.contains("why") && (msg.contains("hard") || msg.contains("difficult")) -> {
                "🧠 **Why Quitting $habit is Hard:**\n\n1. **Dopamine Loops**: Your brain expects the dopamine hit from $habit\n2. **Neural Pathways**: Years of repetition created strong brain connections\n3. **Habit Loops**: Trigger → Routine → Reward cycle is automatic\n\n✨ **The Good News**: Every time you resist, you weaken these pathways and build new, healthier ones!\n\n💪 $username, understanding your enemy makes you stronger!"
            }

            msg.contains("tips") || msg.contains("advice") || msg.contains("how to") -> {
                getRandomTip(username, habit)
            }

            msg.contains("science") || msg.contains("brain") || msg.contains("neuroscience") -> {
                getBrainScience(username, habit)
            }

            else -> null
        }
    }

    private fun getEducationalMotivation(username: String, habit: String, reason: String, days: Int): String {
        val motivations = listOf(
            "🔥 $username, you're amazing! $days days clean!\n\n📚 **Neuroplasticity Fact**: Your brain is literally changing right now. Each day without $habit strengthens your prefrontal cortex (willpower center) and weakens addiction pathways.\n\nRemember: $reason 🌟",

            "💪 $username - $days days strong!\n\n🧬 **Science**: After just 21 days, new neural pathways become more automatic. You're building a new, healthier version of yourself at the cellular level!\n\nYour why: $reason ✨",

            "🌟 $username, incredible progress - $days days!\n\n⚡ **Willpower Science**: Each time you resist $habit, your self-control muscle gets stronger. You're literally training your brain like a muscle!\n\nStay focused on: $reason 🎯",

            "🎉 $username - $days days of freedom!\n\n🔄 **Habit Loop Breaking**: You've successfully interrupted thousands of automatic habit loops. Your brain is learning new, healthier patterns!\n\nYour motivation: $reason 💎"
        )

        return motivations.random()
    }

    private fun getRandomTip(username: String, habit: String): String {
        val tips = listOf(
            "💡 **Tip for $username**: The '10-Minute Rule'\n\nWhen you feel an urge, tell yourself you'll wait just 10 minutes. Often, the urge passes in this time. This trains your brain that urges are temporary!\n\n🧠 Science: Urges follow a wave pattern - they build up, peak, then naturally fade.",

            "🎯 **Strategy for $username**: Change Your Environment\n\nPhysically move to a different room/location when you feel triggered. Your brain associates certain spaces with $habit.\n\n📚 Research shows: Environmental cues trigger 45% of our daily behaviors!",

            "⚡ **Power Technique**: The STOP Method\n\nS - Stop what you're doing\nT - Take a deep breath  \nO - Observe the urge without judgment\nP - Proceed with a healthy alternative\n\n🧘 Mindfulness breaks the automatic response cycle!",

            "🔧 **Brain Hack**: Replacement Habits\n\nDon't just stop $habit - replace it! When you feel the urge, do 20 push-ups, drink water, or text a friend.\n\n🧠 Your brain needs a new routine to follow the same trigger!"
        )

        return tips.random()
    }

    private fun getBrainScience(username: String, habit: String): String {
        val scienceFacts = listOf(
            "🧠 **Brain Science for $username**:\n\nWhen you resist $habit, your anterior cingulate cortex (decision-making area) gets stronger. It's like doing bicep curls for your willpower!\n\n🔬 Studies show: Just 2 weeks of resistance training can increase gray matter in this region.",

            "⚗️ **Neurotransmitter Facts**:\n\nQuitting $habit rebalances your dopamine, serotonin, and GABA levels. This is why you might feel anxious initially - but it gets SO much better!\n\n📈 Timeline: Most people see mood improvements within 2-4 weeks.",

            "🔄 **Neuroplasticity Magic**:\n\nYour brain can form new neural pathways at ANY age! Every time you choose differently, you're literally rewiring centuries of evolution.\n\n✨ $username, you're stronger than your primitive brain patterns!"
        )

        return scienceFacts.random()
    }

    // Helper functions to avoid repetition
    private fun getUsername(context: Context): String {
        return context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
            .getString("username", "Champ") ?: "Champ"
    }

    private fun getHabit(context: Context): String {
        return context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getString("habit", "your habit") ?: "your habit"
    }

    private fun getReason(context: Context): String {
        return context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getString("reason", "your health") ?: "your health"
    }
}