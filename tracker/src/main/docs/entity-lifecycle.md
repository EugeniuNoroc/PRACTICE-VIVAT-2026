## Жизненный цикл Entity в Hibernate

### Состояния
- `Transient` - объект который **еще** не связан с сессией и не был добавлен под управление ORM, находится в этом состоянии после его создании и до момента сохранения.
- `Persistent` - объект который связан **с** открытой сессией и добавлен под управление ORM, находится в этом состоянии после сохранения объекта пока не закрыта сессия.
- `Detached` - объект который **уже** не связан с сессией, находится в этом состоянии после закрытия сессии.
- `Removed` - объект который помечен для удаления из-под управления ORM, но `DELETE` запрос произойдет только после `flush`, до этого момента все еще существует в `persistence context`.

### Переходы
TRANSIENT → PERSISTENT: em.persist()  
PERSISTENT → DETACHED: em.detach() // конец транзакции  
DETACHED → PERSISTENT: em.merge()  
PERSISTENT → REMOVED: em.remove()  

### Граф
TRANSIENT - persist() -> PERSISTENT - remove() -> REMOVED  
|  
detach() / конец транзакции  
V  
DETACHED  
|  
merge()  
V  
PERSISTENT