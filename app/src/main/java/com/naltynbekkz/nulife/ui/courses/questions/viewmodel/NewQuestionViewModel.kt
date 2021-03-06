package com.naltynbekkz.nulife.ui.courses.questions.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.naltynbekkz.nulife.di.ViewModelAssistedFactory
import com.naltynbekkz.nulife.model.Question
import com.naltynbekkz.nulife.model.Student
import com.naltynbekkz.nulife.model.UserCourse
import com.naltynbekkz.nulife.repository.NotificationsRepository
import com.naltynbekkz.nulife.repository.QuestionsRepository
import com.naltynbekkz.nulife.repository.UserRepository
import com.naltynbekkz.nulife.util.Constants
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class NewQuestionViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val questionsRepository: QuestionsRepository,
    private val notificationsRepository: NotificationsRepository,
    userRepository: UserRepository
) : ViewModel() {

    val question: Question = if (savedStateHandle.get<UserCourse>(Constants.USER_COURSE) != null) {
        Question(userCourse = savedStateHandle[Constants.USER_COURSE]!!)
    } else {
        savedStateHandle[Constants.QUESTION]!!
    }

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<NewQuestionViewModel>


    val user = userRepository.user
    val topics = questionsRepository.getTopics(question)

    fun post(
        question: Question,
        anonymous: Boolean,
        allSections: Boolean,
        success: () -> Boolean,
        failure: () -> Unit,
        images: ArrayList<Uri>,
        done: (Int) -> Unit
    ) {
        if (allSections) {
            question.sectionId = 0L
        }

        question.author = Student(
            user.value!!, anonymous
        )
        question.timestamp = System.currentTimeMillis() / 1000
        questionsRepository.post(
            question,
            fun(id) {
                notificationsRepository.follow(id, "question", success)
            },
            failure, images, done
        )
    }

    fun editQuestion(
        question: Question,
        anonymous: Boolean,
        success: () -> Boolean,
        failure: () -> Unit
    ) {
        question.author = Student(
            user.value!!, anonymous
        )
        question.timestamp = System.currentTimeMillis() / 1000
        questionsRepository.editQuestion(question, success, failure)
    }

}