package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SakaDatabase
import com.example.data.repository.TopicRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SAKA SPEAK", appName)
  }

  @Test
  fun `topic repository generates topics properly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val dao = SakaDatabase.getDatabase(context).practiceDao()
    val repo = TopicRepository(dao)

    assertTrue(repo.curatedTopics.isNotEmpty())
    val randomTopic = repo.getRandomTopic()
    assertNotNull(randomTopic)
    assertTrue(randomTopic.title.isNotBlank())
    assertTrue(randomTopic.points.isNotEmpty())
  }
}
