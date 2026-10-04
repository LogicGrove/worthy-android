# Formato de las copias de Worthy

**Español** | [English](backup-format.en.md) · [Guía avanzada](README.advanced.md)

Worthy exporta JSON UTF-8 mediante el selector de archivos de Android. La versión `1` tiene esta estructura principal:

```json
{
  "backupFormatVersion": 1,
  "exportedAt": 0,
  "goals": [],
  "contributions": []
}
```

`exportedAt` y las fechas de objetivos y aportaciones son milisegundos desde la época Unix. Los identificadores son enteros positivos. El dinero utiliza unidades menores enteras de 64 bits y códigos de moneda ISO 4217; el formato no guarda dinero en coma flotante. Cada aportación referencia un objetivo de la misma copia.

La importación valida el documento completo antes de mostrar la confirmación. Confirmar sustituye todos los objetivos y aportaciones actuales en una transacción Room. Se rechazan campos desconocidos, versiones no compatibles, monedas o campos de objetivo inválidos, identificadores duplicados, cantidades no positivas, fechas inconsistentes, desbordamientos al sumar aportaciones y aportaciones sin objetivo.
