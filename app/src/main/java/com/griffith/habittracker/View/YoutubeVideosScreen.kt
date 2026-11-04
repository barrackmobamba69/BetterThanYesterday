package com.griffith.habittracker.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.griffith.habittracker.Controller.YouTubeController
import com.griffith.habittracker.R

@Composable
fun YoutubeSection() {
    val context = LocalContext.current
    val videos = YouTubeController.getVideos()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) { // Scroll state added
            Text(
                text = "Click to load videos",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            videos.forEach { video ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            YouTubeController.openYoutubeVideo(video.title, context)
                        }
                        .padding(bottom = 16.dp)
                ) {
                    // Different images for different videos
                    Image(
                        painter = painterResource(
                            id = when(video.title) {
                                "The Simplest Daily Routine for Self-Improvement" ->
                                    R.drawable.the_simplest_daily_routine_for_self_improvement
                                "This morning routine is scientifically proven to make you limitless" ->
                                    R.drawable.this_morning_routine_is_scientifically_proven_to_make_you_limitless
                                "How to learn anything faster than everyone" ->
                                    R.drawable.how_to_learn_anything_faster_than_everyone
                                "Feeling lost in your twenties" ->
                                    R.drawable.feeling_lost_in_your_twenties
                                "You waste too much time and it needs to stop" ->
                                    R.drawable.you_waste_too_much_time_and_it_needs_to_stop
                                else -> R.drawable.the_simplest_daily_routine_for_self_improvement
                            }
                        ),
                        contentDescription = video.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

//                if (videos.indexOf(video) < videos.size - 1) {
//                    Divider(modifier = Modifier.padding(vertical = 8.dp))
//                }
            }
        }
    }
}