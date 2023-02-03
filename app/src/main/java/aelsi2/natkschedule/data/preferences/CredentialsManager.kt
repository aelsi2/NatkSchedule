package aelsi2.natkschedule.data.preferences

interface CredentialsManager : CredentialsReader {
    suspend fun storeUsername(value : String?)
    suspend fun storePassword(value : String?)
}