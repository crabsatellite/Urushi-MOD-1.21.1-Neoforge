"""
Migrate 1.20-style recipe `result` blocks to 1.21 format:

- {"result": {"item": "X", "count": N}, ...}  -> {"result": {"id": "X", "count": N}, ...}
- {"result": "X", "count": N, ...}            -> {"result": {"id": "X", "count": N}, ...}
- {"result": "X", ...}                        -> {"result": {"id": "X"}, ...}

Walks src/main/resources/data/urushi/recipe/ and rewrites every .json in place.
Reports per-pattern counts and any files that could not be parsed.
"""
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
RECIPE_DIR = ROOT / "src" / "main" / "resources" / "data" / "urushi" / "recipe"


def migrate_result(data):
    """Returns (changed, pattern_label_or_None)."""
    if not isinstance(data, dict):
        return False, None
    if "result" not in data:
        return False, None
    result = data["result"]

    if isinstance(result, dict):
        if "item" in result and "id" not in result:
            new_result = {}
            for k, v in result.items():
                new_result["id" if k == "item" else k] = v
            data["result"] = new_result
            return True, "object_item_to_id"
        return False, None

    if isinstance(result, str):
        new_result = {"id": result}
        if "count" in data:
            new_result["count"] = data.pop("count")
            label = "string_with_count"
        else:
            label = "string_only"
        data["result"] = new_result
        return True, label

    return False, None


def main():
    if not RECIPE_DIR.is_dir():
        print(f"FATAL: recipe dir not found: {RECIPE_DIR}")
        return 1
    files = sorted(RECIPE_DIR.glob("*.json"))
    counts = {"object_item_to_id": 0, "string_with_count": 0, "string_only": 0}
    skipped = []
    parse_errors = []
    changed_files = 0
    for f in files:
        try:
            text = f.read_text(encoding="utf-8")
            data = json.loads(text)
        except Exception as e:
            parse_errors.append(f"{f.name}: {e}")
            continue
        changed, label = migrate_result(data)
        if not changed:
            skipped.append(f.name)
            continue
        counts[label] += 1
        f.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
        changed_files += 1
    print(f"Total files:          {len(files)}")
    print(f"Files modified:       {changed_files}")
    print(f"  object_item_to_id:  {counts['object_item_to_id']}")
    print(f"  string_with_count:  {counts['string_with_count']}")
    print(f"  string_only:        {counts['string_only']}")
    print(f"Skipped (no change):  {len(skipped)}")
    print(f"Parse errors:         {len(parse_errors)}")
    if parse_errors:
        print("---PARSE ERRORS---")
        for e in parse_errors[:20]:
            print(f"  {e}")
    if skipped and len(skipped) <= 30:
        print("---SKIPPED FILES---")
        for s in skipped:
            print(f"  {s}")
    return 0 if not parse_errors else 1


if __name__ == "__main__":
    sys.exit(main())
