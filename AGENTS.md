# AGENTS Instructions

## Workflow Rules
- Do not create new branches; commit directly to the main branch.
- Use git to stage and commit changes.
- After modifying files, run the appropriate tests or programmatic checks.
- Ensure the working tree is clean before concluding work.

## Task Execution Instructions
- Use ripgrep (`rg`) for searching the repository; avoid `ls -R` or `grep -R`.
- Follow instructions in any `AGENTS.md` files whose scope includes the files you touch.
- Run all available checks after code changes and address any failures.
- After committing changes, use the `make_pr` tool to open a pull request.

## Discussion Points
- PR descriptions or commit messages must include sections:
  - **Log new insights**: capture discoveries or decisions made during the work.
  - **Document past failures**: note issues encountered and how they were resolved.
  Update these sections with each contribution to keep project history clear.
