# Contributing

## Workflow
Our project follows the git-flow. This means that we have a `master` (release) branch, a `stage` (testing) branch, and a `develop` branch. All proposed changes must be targeted at the `develop` branch.

Bug fixes and new features must be associated with an issue in GitHub. If an issue does not exist yet, create one and follow the **Guidelines** below. Be sure to assign yourself to the issue; this helps everyone know when an issue is being worked on. If you are no longer working on an issue, please remove yourself from the assign and make a comment with some information to help whoever will work on the issue next.

Create a local branch off `develop`, following this naming convention: `SWE<issue#>-Issue-Title-Goes-Here`.

The `master`, `stage`, and `develop` branches are all protected. Changes must be reviewed and merged by a third party. You can **NOT** merge changes into any of these branches yourself.

## Guidelines
##### Reporting an Issue
Did you notice an issue? We probably missed something. Let us know by opening an issue. Be sure to check whether the issue has already been brought up (expand upon the issue if you have more details), or whether the issue has been fixed in a new release.

In creating a new issue, ensure you use a succinct title, full description, and images if applicable. Also ensure you label the issue according to its type (UI, security, improvement, feature, etc).

If you think you can fix the issue yourself, assign yourself the issue and start working! If you feel the issue is outside of your comfort zone, you can mention someone in the description or assign the issue to another developer.

##### Creating a Pull Request
Once you think your changes are ready to be merged, create a pull request. Use a succinct title, full description, and images if applicable. Linking a pull request to the original issue helps the reviewer when testing your changes. Make sure you label the issue according to its type (UI, security, improvement, feature, arch, etc). Provide a description of how you tested your changes locally.

Another developer will review your changes, provide feedback, and merge once everything looks good.

##### Reviewing a Pull Request
Reviewing a pull request is an important part of the development cycle. As a reviewer, you must make sure that no new issues will be introduced once the changes are merged. You will need to test the changes rigorously.

You should also ensure that the proposed changes fit our coding style. In other words, examine the changes and determine if the code follows the principles of software development outlined below.

It is recommended to list your test steps in a comment on the pull request.

##### Code Style
We follow several principles of software development:
- DRY (don't repeat yourself)
- Cohesion
- Loose Coupling
- Separation of Concerns

If we feel your changes don't follow these principles, we may kindly ask you to modify your logic. If you disagree, you can discuss your changes with your reviewer until an agreement is reached.

Don't be offended if your reviewer requests some changes. Having an external reviewer is a powerful tool, providing an unbiased view on the proposed changes. We all want the same thing, and that is to develop a strong product we can all be proud of.
