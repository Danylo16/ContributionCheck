#!/usr/bin/env python3
"""Generate deterministic ContributionCheck CSV datasets."""

from __future__ import annotations

import argparse
import csv
import random
from decimal import Decimal, ROUND_HALF_UP
from pathlib import Path


HEADERS = (
    "employeeId",
    "contributionMonth",
    "grossSalary",
    "employeeContribution",
    "employerContribution",
    "currency",
)
CENT = Decimal("0.01")


def money(value: Decimal) -> str:
    return str(value.quantize(CENT, rounding=ROUND_HALF_UP))


def build_valid_row(index: int, month: str, rng: random.Random) -> dict[str, str]:
    salary = Decimal(rng.randrange(2200, 6501, 50))
    employee_rate = Decimal(rng.choice((4, 5, 6))) / Decimal(100)
    employer_rate = Decimal(rng.choice((4, 5, 6))) / Decimal(100)

    return {
        "employeeId": f"EMP-{index + 1:05d}",
        "contributionMonth": month,
        "grossSalary": money(salary),
        "employeeContribution": money(salary * employee_rate),
        "employerContribution": money(salary * employer_rate),
        "currency": "EUR",
    }


def make_invalid(
    row: dict[str, str],
    previous_row: dict[str, str],
    error_type: str,
) -> None:
    if error_type == "negative":
        row["employeeContribution"] = f"-{row['employeeContribution']}"
    elif error_type == "currency":
        row["currency"] = "USD"
    elif error_type == "exceeds_salary":
        salary = Decimal(row["grossSalary"])
        row["employeeContribution"] = money(salary * Decimal("0.60"))
        row["employerContribution"] = money(salary * Decimal("0.60"))
    elif error_type == "duplicate":
        row["employeeId"] = previous_row["employeeId"]
        row["contributionMonth"] = previous_row["contributionMonth"]
    else:
        raise ValueError(f"Unknown error type: {error_type}")


def generate_rows(
    count: int,
    month: str,
    invalid_rate: float,
    seed: int,
) -> list[dict[str, str]]:
    if count < 1:
        raise ValueError("--rows must be at least 1")
    if not 0.0 <= invalid_rate <= 1.0:
        raise ValueError("--invalid-rate must be between 0 and 1")

    rng = random.Random(seed)
    rows = [build_valid_row(index, month, rng) for index in range(count)]

    invalid_count = min(round(count * invalid_rate), max(count - 1, 0))
    invalid_indexes = rng.sample(range(1, count), invalid_count)
    error_types = ("negative", "currency", "exceeds_salary", "duplicate")

    for position, row_index in enumerate(invalid_indexes):
        make_invalid(
            rows[row_index],
            rows[row_index - 1],
            error_types[position % len(error_types)],
        )

    return rows


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Generate valid or mixed ContributionCheck CSV test data."
    )
    parser.add_argument("--rows", type=int, default=100, help="Number of data rows")
    parser.add_argument("--month", default="2026-07", help="Month in YYYY-MM format")
    parser.add_argument(
        "--invalid-rate",
        type=float,
        default=0.0,
        help="Share of intentionally invalid rows, from 0 to 1",
    )
    parser.add_argument("--seed", type=int, default=42, help="Random seed")
    parser.add_argument(
        "--output",
        type=Path,
        default=Path("generated-contributions.csv"),
        help="Output CSV path",
    )
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    rows = generate_rows(args.rows, args.month, args.invalid_rate, args.seed)

    args.output.parent.mkdir(parents=True, exist_ok=True)
    with args.output.open("w", newline="", encoding="utf-8") as output_file:
        writer = csv.DictWriter(output_file, fieldnames=HEADERS)
        writer.writeheader()
        writer.writerows(rows)

    invalid_count = round(args.rows * args.invalid_rate)
    print(
        f"Created {args.output} with {len(rows)} rows "
        f"(requested invalid rows: {min(invalid_count, max(args.rows - 1, 0))})."
    )


if __name__ == "__main__":
    main()
