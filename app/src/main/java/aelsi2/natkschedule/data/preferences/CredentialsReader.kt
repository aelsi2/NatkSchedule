package aelsi2.natkschedule.data.preferences

import kotlinx.coroutines.flow.Flow

interface CredentialsReader {
    val username : Flow<String?>
    val password : Flow<String?>
}