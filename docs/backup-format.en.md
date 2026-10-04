# Worthy backup format

[Español](backup-format.md) | **English** · [Advanced guide](README.advanced.en.md)

Worthy exports UTF-8 JSON through Android's Storage Access Framework. Format version `1` has this top-level shape:

```json
{
  "backupFormatVersion": 1,
  "exportedAt": 0,
  "goals": [],
  "contributions": []
}
```

`exportedAt`, goal timestamps, and contribution timestamps are Unix epoch milliseconds. Goal and contribution IDs are positive integers. Monetary fields are signed 64-bit integer minor units and retain their ISO 4217 currency code; the format never stores floating-point money. Every contribution references a goal in the same backup.

Import version 1 validates the complete document before showing confirmation. Confirming replaces all current goals and contributions in one Room transaction. Unknown fields, unsupported versions, invalid currencies, invalid goal fields, duplicate IDs, non-positive amounts, timestamp inconsistencies, overflowed contribution totals, and orphan contributions are rejected.
