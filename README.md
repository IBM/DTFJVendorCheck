# DTFJVendorCheck

Command line program to determine [DTFJ](https://eclipse.dev/openj9/docs/interface_dtfj/) diagnostic vendor.

## Usage

1. Download `DTFJVendorCheck.class`
2. Execute (where `file` is a J9 diagnostic such as a core dump or PHD)
   ```
   java DTFJVendorCheck file
   ```
3. Use the return code (e.g. `$?`)

Return codes:

1. File not specified
2. Some exception caught
3. The dump was produced by IBM Java
4. The dump was produced by Eclipse OpenJ9 (non-IBM Java) or Java &gt;= 10
5. The dump was produced by an unknown vendor
6. No DTFJ dump found
7. Stored version string is null

## Development

```
javac -source 1.8 -target 1.8 DTFJVendorCheck.java
```

## Files

* [LICENSE](LICENSE)
* [CONTRIBUTING.md](CONTRIBUTING.md)
* [MAINTAINERS.md](MAINTAINERS.md)
* [CHANGELOG.md](CHANGELOG.md)

## Notes

This is provided as is without any warranty or support but we will do our best to respond to [issues reported in GitHub][issues] as time permits.

Pull requests are very welcome! Make sure your patches are well tested.
Ideally create a topic branch for every separate change you make. For
example:

1. Fork the repo
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Added some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

## License

All source files must include a Copyright and License header. The SPDX license header is 
preferred because it can be easily scanned.

If you would like to see the detailed LICENSE click [here](LICENSE).

```text
#
# Copyright IBM Corporation. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
#
```
## Authors

- Author: Kevin Grigorenko - <mailto:kevin.grigorenko@us.ibm.com>

[issues]: https://github.com/IBM/DTFJVendorCheck/issues/new
