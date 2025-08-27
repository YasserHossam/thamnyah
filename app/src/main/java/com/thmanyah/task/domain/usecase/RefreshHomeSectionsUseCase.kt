package com.thmanyah.task.domain.usecase

import com.thmanyah.task.domain.model.ContentSection
import com.thmanyah.task.domain.model.Result
import com.thmanyah.task.domain.repository.HomeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshHomeSectionsUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(): Result<List<ContentSection>> {
        return homeRepository.refreshHomeSections()
    }
}