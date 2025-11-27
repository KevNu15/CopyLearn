package Data

/**
 * Singleton que mantiene una única instancia de MemoryDataManager
 * compartida entre todas las Activities de la aplicación.
 *
 * Esto resuelve el problema de que cada Activity creaba su propio
 * MemoryDataManager, causando que los datos no se compartieran.
 */
object DataManagerSingleton {
    private val instance: IDataManager = MemoryDataManager()

    fun getInstance(): IDataManager = instance
}