package uz.unidev.labirint

interface Repository {
    fun loadMap()
    fun getMapByLevel(level: Int): Array<Array<Int>>
}